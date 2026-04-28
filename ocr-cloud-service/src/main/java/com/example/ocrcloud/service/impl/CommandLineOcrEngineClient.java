package com.example.ocrcloud.service.impl;

import com.example.ocrcloud.config.OcrProperties;
import com.example.ocrcloud.service.OcrEngineClient;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component("commandLineEngine")
public class CommandLineOcrEngineClient implements OcrEngineClient {

    private final OcrProperties properties;

    public CommandLineOcrEngineClient(OcrProperties properties) {
        this.properties = properties;
    }

    @Override
    public String engineName() {
        return "tesseract-cli";
    }

    @Override
    public String recognize(File file, String language) {
        try {
            Path tempDir = Path.of(properties.getTempDir());
            Files.createDirectories(tempDir);

            Path outputBase = tempDir.resolve(file.getName() + "-" + System.currentTimeMillis());
            List<String> command = new ArrayList<>();
            command.add(properties.getTesseractCommand());
            command.add(file.getAbsolutePath());
            command.add(outputBase.toAbsolutePath().toString());
            command.add("-l");
            command.add(language);
            command.add("--psm");
            command.add(String.valueOf(properties.getPsm()));
            command.add("--oem");
            command.add(String.valueOf(properties.getOem()));

            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            boolean finished = process.waitFor(properties.getTimeoutSeconds(), TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("命令行 OCR 超时: " + Duration.ofSeconds(properties.getTimeoutSeconds()));
            }
            if (process.exitValue() != 0) {
                throw new RuntimeException("命令行 OCR 执行失败，退出码: " + process.exitValue());
            }

            Path txtFile = Path.of(outputBase + ".txt");
            String text = Files.readString(txtFile, StandardCharsets.UTF_8);
            Files.deleteIfExists(txtFile);
            return text;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("命令行 OCR 中断: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("命令行 OCR 异常: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean health() {
        try {
            Process process = new ProcessBuilder(properties.getTesseractCommand(), "--version")
                    .redirectErrorStream(true)
                    .start();
            return process.waitFor(5, TimeUnit.SECONDS) && process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
