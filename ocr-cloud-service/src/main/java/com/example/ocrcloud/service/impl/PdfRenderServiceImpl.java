package com.example.ocrcloud.service.impl;

import com.example.ocrcloud.config.OcrProperties;
import com.example.ocrcloud.service.PdfRenderService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfRenderServiceImpl implements PdfRenderService {

    private final OcrProperties properties;

    public PdfRenderServiceImpl(OcrProperties properties) {
        this.properties = properties;
    }

    @Override
    public List<BufferedImage> render(Path pdfPath) {
        try (PDDocument document = Loader.loadPDF(pdfPath.toFile())) {
            PDFRenderer renderer = new PDFRenderer(document);
            List<BufferedImage> result = new ArrayList<>();
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, properties.getPdfDpi(), ImageType.RGB);
                result.add(image);
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("PDF 转图片失败", e);
        }
    }

    @Override
    public int countPages(Path pdfPath) {
        try (PDDocument document = Loader.loadPDF(pdfPath.toFile())) {
            return document.getNumberOfPages();
        } catch (IOException e) {
            throw new RuntimeException("读取 PDF 页数失败", e);
        }
    }
}
