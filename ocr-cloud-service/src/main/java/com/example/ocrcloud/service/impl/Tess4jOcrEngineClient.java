package com.example.ocrcloud.service.impl;

import com.example.ocrcloud.config.OcrProperties;
import com.example.ocrcloud.service.OcrEngineClient;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Component("tess4jEngine")
public class Tess4jOcrEngineClient implements OcrEngineClient {

    private final OcrProperties properties;

    public Tess4jOcrEngineClient(OcrProperties properties) {
        this.properties = properties;
    }

    @Override
    public String engineName() {
        return "tess4j";
    }

    @Override
    public String recognize(File file, String language) {
        try {
            Tesseract tesseract = createTesseract(language);
            return tesseract.doOCR(file);
        } catch (TesseractException e) {
            throw new RuntimeException("Tess4J 识别失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean health() {
        return Files.exists(Path.of(properties.getDatapath()));
    }

    private Tesseract createTesseract(String language) {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(properties.getDatapath());
        tesseract.setLanguage(language == null || language.isBlank() ? properties.getLanguage() : language);
        tesseract.setPageSegMode(properties.getPsm());
        tesseract.setOcrEngineMode(properties.getOem());
        return tesseract;
    }
}
