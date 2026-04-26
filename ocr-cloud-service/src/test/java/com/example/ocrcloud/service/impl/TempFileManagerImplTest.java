package com.example.ocrcloud.service.impl;

import com.example.ocrcloud.config.OcrProperties;
import com.example.ocrcloud.model.FileType;
import com.example.ocrcloud.model.OcrTaskContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

class TempFileManagerImplTest {

    @Test
    void shouldPreventPathTraversalFromOriginalFilename() throws Exception {
        OcrProperties properties = new OcrProperties();
        properties.setTempDir(Files.createTempDirectory("ocr-test-").toString());
        properties.setTaskTimeoutSeconds(60);

        TempFileManagerImpl manager = new TempFileManagerImpl(properties);
        OcrTaskContext context = manager.createContext("chi_sim+eng", "x", FileType.IMAGE);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "../../etc/passwd",
                "text/plain",
                "demo".getBytes(StandardCharsets.UTF_8)
        );

        Path saved = manager.saveUpload(file, context);
        Assertions.assertTrue(saved.normalize().startsWith(context.getTaskTempDir().normalize()));
        Assertions.assertEquals(context.getTaskTempDir().normalize(), saved.getParent().normalize());

        manager.cleanup(context);
    }
}
