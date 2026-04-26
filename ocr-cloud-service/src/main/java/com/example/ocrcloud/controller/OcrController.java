package com.example.ocrcloud.controller;

import com.example.ocrcloud.dto.ApiResponse;
import com.example.ocrcloud.dto.OcrResult;
import com.example.ocrcloud.service.OcrHealthService;
import com.example.ocrcloud.service.OcrService;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/ocr")
public class OcrController {

    private final OcrService ocrService;
    private final OcrHealthService ocrHealthService;

    public OcrController(OcrService ocrService, OcrHealthService ocrHealthService) {
        this.ocrService = ocrService;
        this.ocrHealthService = ocrHealthService;
    }

    @PostMapping("/image")
    public ApiResponse<OcrResult> imageOcr(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "language", required = false)
            @Pattern(regexp = "^(chi_sim|eng|chi_sim\\+eng|eng\\+chi_sim)?$", message = "language 仅支持 chi_sim、eng、chi_sim+eng")
            String language) {
        return ApiResponse.ok(ocrService.recognize(file, language));
    }

    @PostMapping("/pdf")
    public ApiResponse<OcrResult> pdfOcr(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "language", required = false)
            @Pattern(regexp = "^(chi_sim|eng|chi_sim\\+eng|eng\\+chi_sim)?$", message = "language 仅支持 chi_sim、eng、chi_sim+eng")
            String language) {
        return ApiResponse.ok(ocrService.recognizePdf(file, language));
    }

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.ok(ocrHealthService.check());
    }
}
