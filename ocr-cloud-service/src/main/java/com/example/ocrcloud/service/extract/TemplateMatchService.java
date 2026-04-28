package com.example.ocrcloud.service.extract;

import com.example.ocrcloud.model.template.ReportTemplate;

public interface TemplateMatchService {
    ReportTemplate match(String cleanedText);
}
