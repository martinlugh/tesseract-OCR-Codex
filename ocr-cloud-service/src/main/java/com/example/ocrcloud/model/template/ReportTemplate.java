package com.example.ocrcloud.model.template;

import java.util.ArrayList;
import java.util.List;

public class ReportTemplate {
    private String templateCode;
    private String documentType;
    private List<String> matchKeywords = new ArrayList<>();
    private List<FieldRule> fields = new ArrayList<>();

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public List<String> getMatchKeywords() {
        return matchKeywords;
    }

    public void setMatchKeywords(List<String> matchKeywords) {
        this.matchKeywords = matchKeywords;
    }

    public List<FieldRule> getFields() {
        return fields;
    }

    public void setFields(List<FieldRule> fields) {
        this.fields = fields;
    }
}
