package com.example.ocrcloud.service.extract;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OcrTextCleanServiceImplTest {

    private final OcrTextCleanService service = new OcrTextCleanServiceImpl();

    @Test
    void shouldNormalizeSymbolsAndSpaces() {
        String text = "空腹血糖 ： 5.3Ｏ mmol／L";
        String cleaned = service.cleanRawText(text);
        Assertions.assertTrue(cleaned.contains(":"));
        Assertions.assertTrue(cleaned.contains("5.3"));
    }
}
