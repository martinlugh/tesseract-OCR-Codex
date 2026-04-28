package com.example.ocrcloud.service.extract;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OcrTextCleanServiceImpl implements OcrTextCleanService {

    @Override
    public String cleanRawText(String rawText) {
        if (rawText == null) {
            return "";
        }
        String cleaned = rawText.replace("：", ":")
                .replace("，", ",")
                .replace("；", ";")
                .replace("（", "(")
                .replace("）", ")")
                .replace("Ｏ", "0")
                .replace("o", "0")
                .replace("l", "1")
                .replace("I", "1")
                .replaceAll("[\\t\\r]+", " ")
                .replaceAll("[ ]{2,}", " ");
        return cleaned;
    }

    @Override
    public List<String> normalizeLines(String rawText) {
        String cleaned = cleanRawText(rawText);
        String[] lines = cleaned.split("\\n");
        List<String> normalized = new ArrayList<>();
        for (String line : lines) {
            String val = line.trim().replaceAll("\\s*:\\s*", ":");
            if (!val.isBlank()) {
                normalized.add(val);
            }
        }
        return normalized;
    }
}
