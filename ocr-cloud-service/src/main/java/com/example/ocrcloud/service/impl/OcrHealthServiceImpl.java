package com.example.ocrcloud.service.impl;

import com.example.ocrcloud.config.OcrProperties;
import com.example.ocrcloud.service.OcrEngineClient;
import com.example.ocrcloud.service.OcrHealthService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class OcrHealthServiceImpl implements OcrHealthService {

    private final OcrProperties properties;
    private final OcrEngineClient tess4jEngine;
    private final OcrEngineClient commandLineEngine;

    public OcrHealthServiceImpl(OcrProperties properties,
                                @Qualifier("tess4jEngine") OcrEngineClient tess4jEngine,
                                @Qualifier("commandLineEngine") OcrEngineClient commandLineEngine) {
        this.properties = properties;
        this.tess4jEngine = tess4jEngine;
        this.commandLineEngine = commandLineEngine;
    }

    @Override
    public Map<String, Object> check() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("datapath", properties.getDatapath());
        data.put("datapathExists", Files.exists(Path.of(properties.getDatapath())));
        data.put("language", properties.getLanguage());
        data.put("tess4j", tess4jEngine.health());
        data.put("tesseractCli", commandLineEngine.health());
        data.put("preferTess4j", properties.isPreferTess4j());
        data.put("maxFileSizeMb", properties.getMaxFileSizeMb());
        data.put("maxPages", properties.getMaxPages());
        data.put("taskTimeoutSeconds", properties.getTaskTimeoutSeconds());
        return data;
    }
}
