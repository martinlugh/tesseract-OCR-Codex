package com.example.ocrcloud.service.impl;

import com.example.ocrcloud.service.OcrEngineClient;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Component;

import java.io.File;

@Component("tess4jEngine")
public class Tess4jOcrEngineClient implements OcrEngineClient {

    private final Tesseract tesseract;

    public Tess4jOcrEngineClient(Tesseract tesseract) {
        this.tesseract = tesseract;
    }

    @Override
    public String engineName() {
        return "tess4j";
    }

    @Override
    public String recognize(File file, String language) {
        try {
            tesseract.setLanguage(language);
            return tesseract.doOCR(file);
        } catch (TesseractException e) {
            throw new RuntimeException("Tess4J 识别失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean health() {
        return tesseract.getDatapath() != null && !tesseract.getDatapath().isBlank();
    }
}
