package com.example.ocrcloud.model;

import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

public class OcrTaskContext {
    private final String taskId;
    private final Instant startTime;
    private final long deadlineEpochMs;
    private final String language;
    private final FileType fileType;
    private final String fileName;
    private final Path taskTempDir;

    public OcrTaskContext(long timeoutSeconds, String language, FileType fileType, String fileName, Path taskTempDir) {
        this.taskId = UUID.randomUUID().toString();
        this.startTime = Instant.now();
        this.deadlineEpochMs = System.currentTimeMillis() + timeoutSeconds * 1000L;
        this.language = language;
        this.fileType = fileType;
        this.fileName = fileName;
        this.taskTempDir = taskTempDir;
    }

    public void assertNotTimeout() {
        if (System.currentTimeMillis() > deadlineEpochMs) {
            throw new RuntimeException("OCR 任务超时，taskId=" + taskId);
        }
    }

    public long remainingMs() {
        return Math.max(1, deadlineEpochMs - System.currentTimeMillis());
    }

    public String getTaskId() {
        return taskId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public String getLanguage() {
        return language;
    }

    public FileType getFileType() {
        return fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public Path getTaskTempDir() {
        return taskTempDir;
    }
}
