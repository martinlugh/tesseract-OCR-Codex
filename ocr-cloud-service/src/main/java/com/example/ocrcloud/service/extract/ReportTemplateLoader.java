package com.example.ocrcloud.service.extract;

import com.example.ocrcloud.model.template.ReportTemplate;

import java.util.List;

public interface ReportTemplateLoader {
    List<ReportTemplate> getTemplates();
}
