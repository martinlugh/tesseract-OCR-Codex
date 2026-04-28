package com.example.ocrcloud.monitor;

import com.example.ocrcloud.config.OcrProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

@Component
public class TempFileCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(TempFileCleanupScheduler.class);
    private final OcrProperties properties;

    public TempFileCleanupScheduler(OcrProperties properties) {
        this.properties = properties;
    }

    @Scheduled(fixedDelay = 600000)
    public void cleanup() {
        Path base = Path.of(properties.getTempDir());
        if (!Files.exists(base)) {
            return;
        }
        Instant expireTime = Instant.now().minus(properties.getTempFileRetentionMinutes(), ChronoUnit.MINUTES);
        try (var stream = Files.list(base)) {
            stream.filter(Files::isDirectory).forEach(dir -> deleteIfExpired(dir, expireTime));
        } catch (IOException e) {
            log.warn("临时目录清理失败", e);
        }
    }

    private void deleteIfExpired(Path dir, Instant expireTime) {
        try {
            FileTime time = Files.getLastModifiedTime(dir);
            if (time.toInstant().isAfter(expireTime)) {
                return;
            }
            try (var walk = Files.walk(dir)) {
                walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ignored) {
                    }
                });
            }
            log.info("已清理过期临时目录 path={}", dir);
        } catch (IOException e) {
            log.warn("删除临时目录失败 path={}", dir, e);
        }
    }
}
