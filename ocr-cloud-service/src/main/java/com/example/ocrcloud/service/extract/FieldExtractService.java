package com.example.ocrcloud.service.extract;

import com.example.ocrcloud.model.OcrPageResult;
import com.example.ocrcloud.model.template.StructuredReportResult;

import java.util.List;

public interface FieldExtractService {
    StructuredReportResult extract(List<OcrPageResult> pages, String rawText);
}
