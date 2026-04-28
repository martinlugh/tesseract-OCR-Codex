package com.example.ocrcloud.service;

import com.example.ocrcloud.model.OcrTaskContext;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

public interface TempFileManager {
    OcrTaskContext createContext(String language, String fileName, com.example.ocrcloud.model.FileType fileType);

    Path saveUpload(MultipartFile file, OcrTaskContext context);

    Path saveBufferedImage(BufferedImage image, String prefix, OcrTaskContext context);

    void cleanup(OcrTaskContext context);
}
