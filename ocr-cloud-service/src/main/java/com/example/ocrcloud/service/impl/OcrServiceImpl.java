package com.example.ocrcloud.service.impl;

import com.example.ocrcloud.config.OcrProperties;
import com.example.ocrcloud.dto.OcrResult;
import com.example.ocrcloud.enums.ErrorCode;
import com.example.ocrcloud.exception.BusinessException;
import com.example.ocrcloud.model.FileType;
import com.example.ocrcloud.model.OcrPageResult;
import com.example.ocrcloud.model.OcrTaskContext;
import com.example.ocrcloud.model.ProcessStatus;
import com.example.ocrcloud.service.ImagePreprocessService;
import com.example.ocrcloud.service.OcrEngineClient;
import com.example.ocrcloud.service.OcrService;
import com.example.ocrcloud.service.PdfRenderService;
import com.example.ocrcloud.service.TempFileManager;
import com.example.ocrcloud.service.extract.FieldExtractService;
import com.example.ocrcloud.util.FileTypeDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.*;

@Service
public class OcrServiceImpl implements OcrService {

    private static final Logger log = LoggerFactory.getLogger(OcrServiceImpl.class);

    private final OcrProperties properties;
    private final OcrEngineClient tess4jEngine;
    private final OcrEngineClient commandLineEngine;
    private final FileTypeDetector fileTypeDetector;
    private final PdfRenderService pdfRenderService;
    private final ImagePreprocessService imagePreprocessService;
    private final TempFileManager tempFileManager;
    private final FieldExtractService fieldExtractService;
    private final Executor executor;

    public OcrServiceImpl(OcrProperties properties,
                          @Qualifier("tess4jEngine") OcrEngineClient tess4jEngine,
                          @Qualifier("commandLineEngine") OcrEngineClient commandLineEngine,
                          FileTypeDetector fileTypeDetector,
                          PdfRenderService pdfRenderService,
                          ImagePreprocessService imagePreprocessService,
                          TempFileManager tempFileManager,
                          FieldExtractService fieldExtractService,
                          @Qualifier("ocrExecutor") Executor executor) {
        this.properties = properties;
        this.tess4jEngine = tess4jEngine;
        this.commandLineEngine = commandLineEngine;
        this.fileTypeDetector = fileTypeDetector;
        this.pdfRenderService = pdfRenderService;
        this.imagePreprocessService = imagePreprocessService;
        this.tempFileManager = tempFileManager;
        this.fieldExtractService = fieldExtractService;
        this.executor = executor;
    }

    @Override
    public OcrResult recognize(MultipartFile file, String language) {
        validateUpload(file, FileType.IMAGE);
        return processFile(file, normalizeLanguage(language), FileType.IMAGE);
    }

    @Override
    public OcrResult recognizePdf(MultipartFile file, String language) {
        validateUpload(file, FileType.PDF);
        return processFile(file, normalizeLanguage(language), FileType.PDF);
    }

    private OcrResult processFile(MultipartFile file, String language, FileType expectedType) {
        Instant start = Instant.now();
        OcrTaskContext context = null;
        try {
            FileType detected = fileTypeDetector.detect(file);
            if (detected != expectedType) {
                throw new BusinessException(ErrorCode.FILE_TYPE_ERROR, "文件类型不符合接口要求");
            }
            context = tempFileManager.createContext(language, safeName(file), detected);

            long saveStart = System.currentTimeMillis();
            Path uploadPath = tempFileManager.saveUpload(file, context);
            log.info("文件保存完成 taskId={} costMs={} file={}", context.getTaskId(), System.currentTimeMillis() - saveStart, uploadPath);

            List<OcrPageResult> pageResults = detected == FileType.PDF
                    ? handlePdf(uploadPath, context)
                    : handleImage(uploadPath, context);

            OcrResult result = buildResult(context, pageResults, Duration.between(start, Instant.now()).toMillis(), ProcessStatus.SUCCESS);
            log.info("OCR任务完成 taskId={} pages={} totalCostMs={}", context.getTaskId(), result.getTotalPages(), result.getTotalCostMs());
            return result;
        } catch (TimeoutException e) {
            throw new BusinessException(ErrorCode.OCR_TIMEOUT, "OCR 任务超时: " + e.getMessage());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OCR_FAILED, "OCR 处理失败，原因: " + e.getMessage());
        } finally {
            tempFileManager.cleanup(context);
        }
    }

    private List<OcrPageResult> handleImage(Path imagePath, OcrTaskContext context) throws IOException, TimeoutException {
        context.assertNotTimeout();
        BufferedImage source = ImageIO.read(imagePath.toFile());
        if (source == null) {
            throw new BusinessException(ErrorCode.FILE_READ_ERROR, "无法读取图片内容");
        }

        long preprocessStart = System.currentTimeMillis();
        BufferedImage preprocessed = imagePreprocessService.preprocess(source);
        Path processedPath = tempFileManager.saveBufferedImage(preprocessed, "processed-", context);
        log.info("图片预处理完成 taskId={} costMs={}", context.getTaskId(), System.currentTimeMillis() - preprocessStart);

        long ocrStart = System.currentTimeMillis();
        String text = executeOcrWithFallback(processedPath, context);
        long ocrCost = System.currentTimeMillis() - ocrStart;
        log.info("图片OCR完成 taskId={} costMs={}", context.getTaskId(), ocrCost);

        OcrPageResult pageResult = new OcrPageResult(1, ocrCost, context.getLanguage(), text, ProcessStatus.SUCCESS);
        return List.of(pageResult);
    }

    private List<OcrPageResult> handlePdf(Path pdfPath, OcrTaskContext context) throws TimeoutException {
        int pages = pdfRenderService.countPages(pdfPath);
        if (pages > properties.getMaxPages()) {
            throw new BusinessException(ErrorCode.PDF_PAGE_LIMIT, "PDF 页数超过限制，最大页数: " + properties.getMaxPages());
        }

        long renderStart = System.currentTimeMillis();
        List<BufferedImage> images = pdfRenderService.render(pdfPath);
        log.info("PDF渲染完成 taskId={} pages={} costMs={}", context.getTaskId(), images.size(), System.currentTimeMillis() - renderStart);

        List<OcrPageResult> result = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            context.assertNotTimeout();
            long pageStart = System.currentTimeMillis();

            BufferedImage preprocessed = imagePreprocessService.preprocess(images.get(i));
            Path imagePath = tempFileManager.saveBufferedImage(preprocessed, "pdf-page-" + (i + 1), context);
            String text = executeOcrWithFallback(imagePath, context);

            long cost = System.currentTimeMillis() - pageStart;
            log.info("PDF页OCR完成 taskId={} page={} costMs={}", context.getTaskId(), i + 1, cost);
            result.add(new OcrPageResult(i + 1, cost, context.getLanguage(), text, ProcessStatus.SUCCESS));
        }
        return result;
    }

    private String executeOcrWithFallback(Path imagePath, OcrTaskContext context) throws TimeoutException {
        OcrEngineClient preferred = properties.isPreferTess4j() ? tess4jEngine : commandLineEngine;
        OcrEngineClient fallback = properties.isPreferTess4j() ? commandLineEngine : tess4jEngine;
        try {
            return executeWithTimeout(preferred, imagePath, context);
        } catch (Exception ex) {
            log.warn("主引擎失败，切换备用引擎 taskId={} primary={} error={}", context.getTaskId(), preferred.engineName(), ex.getMessage());
            return executeWithTimeout(fallback, imagePath, context);
        }
    }

    private String executeWithTimeout(OcrEngineClient engine, Path imagePath, OcrTaskContext context) throws TimeoutException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> engine.recognize(imagePath.toFile(), context.getLanguage()), executor);
        try {
            return future.get(context.remainingMs(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("OCR 线程被中断", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("OCR 引擎执行异常: " + engine.engineName(), e.getCause());
        } catch (TimeoutException e) {
            future.cancel(true);
            throw e;
        }
    }

    private OcrResult buildResult(OcrTaskContext context, List<OcrPageResult> pageResults, long totalCost, ProcessStatus status) {
        StringJoiner textJoiner = new StringJoiner("\n\n");
        for (OcrPageResult page : pageResults) {
            textJoiner.add(page.getText());
        }

        OcrResult result = new OcrResult();
        result.setFileName(context.getFileName());
        result.setLanguage(context.getLanguage());
        result.setEngine(properties.isPreferTess4j() ? "tess4j+fallback" : "tesseract-cli+fallback");
        result.setText(textJoiner.toString());
        result.setFileType(context.getFileType());
        result.setTotalPages(pageResults.size());
        result.setTotalCostMs(totalCost);
        result.setStatus(status);
        result.setPages(pageResults);

        long extractStart = System.currentTimeMillis();
        result.setStructuredData(fieldExtractService.extract(pageResults, result.getText()));
        long extractCost = System.currentTimeMillis() - extractStart;
        log.info("结构化抽取完成 taskId={} costMs={}", context.getTaskId(), extractCost);
        return result;
    }

    private void validateUpload(MultipartFile file, FileType expectedType) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY, "上传文件不能为空");
        }
        long maxBytes = properties.getMaxFileSizeMb() * 1024L * 1024L;
        if (file.getSize() > maxBytes) {
            throw new BusinessException(ErrorCode.FILE_SIZE_LIMIT, "文件大小超过限制，最大: " + properties.getMaxFileSizeMb() + "MB");
        }
        FileType actual = fileTypeDetector.detect(file);
        if (actual != expectedType) {
            throw new BusinessException(ErrorCode.FILE_TYPE_ERROR, "不支持的文件类型");
        }
    }

    private String normalizeLanguage(String language) {
        if (language == null || language.isBlank()) {
            return properties.getLanguage();
        }
        return language;
    }

    private String safeName(MultipartFile file) {
        return file.getOriginalFilename() == null ? "upload.bin" : file.getOriginalFilename();
    }
}
