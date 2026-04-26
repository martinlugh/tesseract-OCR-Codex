package com.example.ocrcloud.dto;

import com.example.ocrcloud.model.FileType;
import com.example.ocrcloud.model.OcrPageResult;
import com.example.ocrcloud.model.ProcessStatus;
import com.example.ocrcloud.model.template.StructuredReportResult;

import java.util.ArrayList;
import java.util.List;

public class OcrResult {
    private String fileName;
    private String language;
    private String engine;
    private String text;
    private FileType fileType;
    private int totalPages;
    private long totalCostMs;
    private ProcessStatus status;
    private List<OcrPageResult> pages = new ArrayList<>();
    private StructuredReportResult structuredData;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalCostMs() {
        return totalCostMs;
    }

    public void setTotalCostMs(long totalCostMs) {
        this.totalCostMs = totalCostMs;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public List<OcrPageResult> getPages() {
        return pages;
    }

    public void setPages(List<OcrPageResult> pages) {
        this.pages = pages;
    }

    public StructuredReportResult getStructuredData() {
        return structuredData;
    }

    public void setStructuredData(StructuredReportResult structuredData) {
        this.structuredData = structuredData;
    }
}
