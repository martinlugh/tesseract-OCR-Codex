package com.example.ocrcloud.controller;

import com.example.ocrcloud.service.OcrHealthService;
import com.example.ocrcloud.service.OcrService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OcrController.class)
class OcrControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OcrService ocrService;

    @MockBean
    private OcrHealthService ocrHealthService;

    @Test
    void invalidLanguageShouldReturn400() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "a.jpg", MediaType.IMAGE_JPEG_VALUE, "x".getBytes());

        mockMvc.perform(multipart("/api/ocr/image")
                        .file(file)
                        .param("language", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
