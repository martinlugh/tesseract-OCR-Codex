package com.example.ocrcloud.service.extract;

import java.util.List;

public interface OcrTextCleanService {
    String cleanRawText(String rawText);

    List<String> normalizeLines(String rawText);
}
