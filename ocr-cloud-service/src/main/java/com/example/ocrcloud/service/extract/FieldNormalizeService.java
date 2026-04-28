package com.example.ocrcloud.service.extract;

import com.example.ocrcloud.model.template.StructuredField;

public interface FieldNormalizeService {
    StructuredField normalize(StructuredField field);
}
