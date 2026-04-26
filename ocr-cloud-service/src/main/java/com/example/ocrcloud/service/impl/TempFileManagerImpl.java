package com.example.ocrcloud.service.impl;

import com.example.ocrcloud.config.OcrProperties;
import com.example.ocrcloud.model.FileType;
import com.example.ocrcloud.model.OcrTaskContext;
import com.example.ocrcloud.service.TempFileManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

@Service
public class TempFileManagerImpl implements TempFileManager {

    private final OcrProperties properties;

    public TempFileManagerImpl(OcrProperties properties) {
        this.properties = properties;
    }

    @Override
    public OcrTaskContext createContext(String language, String fileName, FileType fileType) {
        try {
            Path baseDir = Path.of(properties.getTempDir());
            Files.createDirectories(baseDir);
            Path taskDir = Files.createTempDirectory(baseDir, "task-");
            return new OcrTaskContext(properties.getTaskTimeoutSeconds(), language, fileType, fileName, taskDir);
        } catch (IOException e) {
            throw new RuntimeException("创建临时目录失败", e);
        }
    }

    @Override
    public Path saveUpload(MultipartFile file, OcrTaskContext context) {
        try {
            String name = file.getOriginalFilename() == null ? "upload.bin" : file.getOriginalFilename();
            Path path = context.getTaskTempDir().resolve(name);
            file.transferTo(path);
            return path;
        } catch (IOException e) {
            throw new RuntimeException("保存上传文件失败", e);
        }
    }

    @Override
    public Path saveBufferedImage(BufferedImage image, String prefix, OcrTaskContext context) {
        try {
            Path path = Files.createTempFile(context.getTaskTempDir(), prefix, ".png");
            ImageIO.write(image, "png", path.toFile());
            return path;
        } catch (IOException e) {
            throw new RuntimeException("保存预处理图片失败", e);
        }
    }

    @Override
    public void cleanup(OcrTaskContext context) {
        if (context == null) {
            return;
        }
        Path path = context.getTaskTempDir();
        if (path == null || !Files.exists(path)) {
            return;
        }
        try (var stream = Files.walk(path)) {
            stream.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {
                }
            });
        } catch (IOException ignored) {
        }
    }
}
