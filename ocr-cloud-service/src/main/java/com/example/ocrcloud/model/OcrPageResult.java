package com.example.ocrcloud.model;

public class OcrPageResult {
    private int pageNo;
    private long costMs;
    private String language;
    private String text;
    private ProcessStatus status;

    public OcrPageResult() {
    }

    public OcrPageResult(int pageNo, long costMs, String language, String text, ProcessStatus status) {
        this.pageNo = pageNo;
        this.costMs = costMs;
        this.language = language;
        this.text = text;
        this.status = status;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public long getCostMs() {
        return costMs;
    }

    public void setCostMs(long costMs) {
        this.costMs = costMs;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }
}
