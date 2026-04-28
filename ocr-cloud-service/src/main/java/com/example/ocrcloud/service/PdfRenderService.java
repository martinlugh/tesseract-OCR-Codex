package com.example.ocrcloud.service;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;

public interface PdfRenderService {
    List<BufferedImage> render(Path pdfPath);

    int countPages(Path pdfPath);
}
