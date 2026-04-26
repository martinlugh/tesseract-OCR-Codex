package com.example.ocrcloud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ocr.engine")
public class OcrProperties {
    private String datapath;
    private String language;
    private int psm;
    private int oem;
    private int timeoutSeconds;
    private String tesseractCommand;
    private String tempDir;
    private boolean preferTess4j;
    private int maxFileSizeMb;
    private int maxPages;
    private int taskTimeoutSeconds;
    private int pdfDpi;
    private int normalizedDpi;
    private int executorCorePoolSize;
    private int executorMaxPoolSize;
    private int executorQueueCapacity;
    private int tempFileRetentionMinutes;

    public String getDatapath() {
        return datapath;
    }

    public void setDatapath(String datapath) {
        this.datapath = datapath;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getPsm() {
        return psm;
    }

    public void setPsm(int psm) {
        this.psm = psm;
    }

    public int getOem() {
        return oem;
    }

    public void setOem(int oem) {
        this.oem = oem;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public String getTesseractCommand() {
        return tesseractCommand;
    }

    public void setTesseractCommand(String tesseractCommand) {
        this.tesseractCommand = tesseractCommand;
    }

    public String getTempDir() {
        return tempDir;
    }

    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }

    public boolean isPreferTess4j() {
        return preferTess4j;
    }

    public void setPreferTess4j(boolean preferTess4j) {
        this.preferTess4j = preferTess4j;
    }

    public int getMaxFileSizeMb() {
        return maxFileSizeMb;
    }

    public void setMaxFileSizeMb(int maxFileSizeMb) {
        this.maxFileSizeMb = maxFileSizeMb;
    }

    public int getMaxPages() {
        return maxPages;
    }

    public void setMaxPages(int maxPages) {
        this.maxPages = maxPages;
    }

    public int getTaskTimeoutSeconds() {
        return taskTimeoutSeconds;
    }

    public void setTaskTimeoutSeconds(int taskTimeoutSeconds) {
        this.taskTimeoutSeconds = taskTimeoutSeconds;
    }

    public int getPdfDpi() {
        return pdfDpi;
    }

    public void setPdfDpi(int pdfDpi) {
        this.pdfDpi = pdfDpi;
    }

    public int getNormalizedDpi() {
        return normalizedDpi;
    }

    public void setNormalizedDpi(int normalizedDpi) {
        this.normalizedDpi = normalizedDpi;
    }

    public int getExecutorCorePoolSize() {
        return executorCorePoolSize;
    }

    public void setExecutorCorePoolSize(int executorCorePoolSize) {
        this.executorCorePoolSize = executorCorePoolSize;
    }

    public int getExecutorMaxPoolSize() {
        return executorMaxPoolSize;
    }

    public void setExecutorMaxPoolSize(int executorMaxPoolSize) {
        this.executorMaxPoolSize = executorMaxPoolSize;
    }

    public int getExecutorQueueCapacity() {
        return executorQueueCapacity;
    }

    public void setExecutorQueueCapacity(int executorQueueCapacity) {
        this.executorQueueCapacity = executorQueueCapacity;
    }

    public int getTempFileRetentionMinutes() {
        return tempFileRetentionMinutes;
    }

    public void setTempFileRetentionMinutes(int tempFileRetentionMinutes) {
        this.tempFileRetentionMinutes = tempFileRetentionMinutes;
    }
}
