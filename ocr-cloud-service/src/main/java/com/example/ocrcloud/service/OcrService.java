package com.example.ocrcloud.service;

import com.example.ocrcloud.dto.OcrResult;
import org.springframework.web.multipart.MultipartFile;

public interface OcrService {
    OcrResult recognize(MultipartFile file, String language);

    OcrResult recognizePdf(MultipartFile file, String language);
}
