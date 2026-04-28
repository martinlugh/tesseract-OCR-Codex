package com.example.ocrcloud.service.extract;

import com.example.ocrcloud.model.template.StructuredField;
import org.springframework.stereotype.Service;

@Service
public class FieldNormalizeServiceImpl implements FieldNormalizeService {

    @Override
    public StructuredField normalize(StructuredField field) {
        if (field.getUnit() != null) {
            field.setUnit(field.getUnit().trim());
        }
        if (field.getReferenceRange() != null) {
            field.setReferenceRange(field.getReferenceRange().replace("~", "-").trim());
        }
        if (field.getAbnormalFlag() != null) {
            String f = field.getAbnormalFlag().trim().toUpperCase();
            if ("↑".equals(f) || "H".equals(f)) {
                field.setAbnormalFlag("HIGH");
            } else if ("↓".equals(f) || "L".equals(f)) {
                field.setAbnormalFlag("LOW");
            }
        }
        return field;
    }
}
