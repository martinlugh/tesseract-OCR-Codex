package com.example.ocrcloud.service.extract;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ReportTemplateLoaderImplTest {

    @Test
    void shouldLoadHealthCheckTemplate() {
        ReportTemplateLoader loader = new ReportTemplateLoaderImpl();
        Assertions.assertFalse(loader.getTemplates().isEmpty());
        Assertions.assertTrue(loader.getTemplates().stream().anyMatch(t -> "HEALTH_CHECK_REPORT".equals(t.getDocumentType())));
    }
}
