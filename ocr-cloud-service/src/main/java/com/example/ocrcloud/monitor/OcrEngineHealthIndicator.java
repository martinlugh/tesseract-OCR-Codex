package com.example.ocrcloud.monitor;

import com.example.ocrcloud.config.OcrProperties;
import com.example.ocrcloud.service.OcrEngineClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component("ocrEngine")
public class OcrEngineHealthIndicator implements HealthIndicator {

    private final OcrProperties properties;
    private final OcrEngineClient tess4jEngine;
    private final OcrEngineClient commandLineEngine;

    public OcrEngineHealthIndicator(OcrProperties properties,
                                    @Qualifier("tess4jEngine") OcrEngineClient tess4jEngine,
                                    @Qualifier("commandLineEngine") OcrEngineClient commandLineEngine) {
        this.properties = properties;
        this.tess4jEngine = tess4jEngine;
        this.commandLineEngine = commandLineEngine;
    }

    @Override
    public Health health() {
        boolean datapathExists = Files.exists(Path.of(properties.getDatapath()));
        boolean tess4jOk = tess4jEngine.health();
        boolean cliOk = commandLineEngine.health();
        boolean up = datapathExists && (tess4jOk || cliOk);
        Health.Builder builder = up ? Health.up() : Health.down();
        return builder.withDetail("datapath", properties.getDatapath())
                .withDetail("datapathExists", datapathExists)
                .withDetail("tess4j", tess4jOk)
                .withDetail("tesseractCli", cliOk)
                .build();
    }
}
