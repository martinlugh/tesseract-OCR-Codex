package com.example.ocrcloud.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OcrProperties.class)
public class TesseractConfig {

    @Bean
    public Tesseract tesseract(OcrProperties properties) {
        // 仅初始化本地 Tesseract 客户端，不修改核心识别逻辑
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(properties.getDatapath());
        tesseract.setLanguage(properties.getLanguage());
        tesseract.setPageSegMode(properties.getPsm());
        tesseract.setOcrEngineMode(properties.getOem());
        return tesseract;
    }
}
