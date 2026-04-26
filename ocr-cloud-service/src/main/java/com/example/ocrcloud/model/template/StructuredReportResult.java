package com.example.ocrcloud.model.template;

import java.util.ArrayList;
import java.util.List;

public class StructuredReportResult {
    private String documentType;
    private List<Integer> pages = new ArrayList<>();
    private String rawText;
    private List<StructuredField> fields = new ArrayList<>();
    private List<String> unmatchedLines = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public List<Integer> getPages() {
        return pages;
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public List<StructuredField> getFields() {
        return fields;
    }

    public void setFields(List<StructuredField> fields) {
        this.fields = fields;
    }

    public List<String> getUnmatchedLines() {
        return unmatchedLines;
    }

    public void setUnmatchedLines(List<String> unmatchedLines) {
        this.unmatchedLines = unmatchedLines;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
}
