package com.example.ocrcloud.service.extract;

import com.example.ocrcloud.model.OcrPageResult;
import com.example.ocrcloud.model.template.FieldRule;
import com.example.ocrcloud.model.template.ReportTemplate;
import com.example.ocrcloud.model.template.StructuredField;
import com.example.ocrcloud.model.template.StructuredReportResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FieldExtractServiceImpl implements FieldExtractService {

    private final OcrTextCleanService ocrTextCleanService;
    private final TemplateMatchService templateMatchService;
    private final FieldNormalizeService fieldNormalizeService;

    public FieldExtractServiceImpl(OcrTextCleanService ocrTextCleanService,
                                   TemplateMatchService templateMatchService,
                                   FieldNormalizeService fieldNormalizeService) {
        this.ocrTextCleanService = ocrTextCleanService;
        this.templateMatchService = templateMatchService;
        this.fieldNormalizeService = fieldNormalizeService;
    }

    @Override
    public StructuredReportResult extract(List<OcrPageResult> pages, String rawText) {
        StructuredReportResult result = new StructuredReportResult();
        String cleanedText = ocrTextCleanService.cleanRawText(rawText);
        List<String> lines = ocrTextCleanService.normalizeLines(cleanedText);
        ReportTemplate template = templateMatchService.match(cleanedText);
        if (template == null) {
            result.setDocumentType("UNKNOWN");
            result.setRawText(cleanedText);
            result.setUnmatchedLines(lines);
            result.getWarnings().add("未匹配到模板");
            return result;
        }

        result.setDocumentType(template.getDocumentType());
        result.setRawText(cleanedText);
        for (int i = 0; i < pages.size(); i++) {
            result.getPages().add(i + 1);
        }

        Set<Integer> matchedIndex = new HashSet<>();
        for (FieldRule rule : template.getFields()) {
            StructuredField field = extractField(rule, lines, pages, matchedIndex);
            if (field != null) {
                StructuredField normalized = fieldNormalizeService.normalize(field);
                result.getFields().add(normalized);
                if (normalized.getConfidence() < 0.75) {
                    result.getWarnings().add("低置信度字段: " + normalized.getFieldName());
                }
            } else {
                result.getWarnings().add("字段未识别: " + rule.getFieldName());
            }
        }

        for (int i = 0; i < lines.size(); i++) {
            if (!matchedIndex.contains(i)) {
                result.getUnmatchedLines().add(lines.get(i));
            }
        }
        return result;
    }

    private StructuredField extractField(FieldRule rule,
                                         List<String> lines,
                                         List<OcrPageResult> pages,
                                         Set<Integer> matchedIndex) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!containsAlias(line, rule.getAliases())) {
                continue;
            }
            for (String patternText : rule.getPatterns()) {
                Pattern pattern = Pattern.compile(patternText, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    StructuredField field = new StructuredField();
                    field.setFieldCode(rule.getFieldCode());
                    field.setFieldName(rule.getFieldName());
                    field.setRawName(readGroup(matcher, "name", rule.getFieldName()));
                    field.setValue(readGroup(matcher, "value", ""));
                    field.setUnit(readGroup(matcher, "unit", ""));
                    field.setReferenceRange(readGroup(matcher, "range", ""));
                    field.setAbnormalFlag(readGroup(matcher, "flag", ""));
                    field.setSourceText(line);
                    field.setSourcePage(findPage(pages, line));
                    field.setConfidence(calcConfidence(line, field));
                    matchedIndex.add(i);
                    return field;
                }
            }
        }
        return null;
    }

    private boolean containsAlias(String line, List<String> aliases) {
        for (String alias : aliases) {
            if (line.contains(alias)) {
                return true;
            }
        }
        return false;
    }

    private int findPage(List<OcrPageResult> pages, String line) {
        for (OcrPageResult page : pages) {
            if (page.getText() != null && page.getText().contains(line)) {
                return page.getPageNo();
            }
        }
        return pages.isEmpty() ? 1 : pages.get(0).getPageNo();
    }

    private String readGroup(Matcher matcher, String name, String defaultValue) {
        try {
            String value = matcher.group(name);
            return value == null ? defaultValue : value.trim();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private double calcConfidence(String line, StructuredField field) {
        double score = 0.5;
        if (field.getValue() != null && !field.getValue().isBlank()) {
            score += 0.3;
        }
        if (field.getUnit() != null && !field.getUnit().isBlank()) {
            score += 0.1;
        }
        if (field.getReferenceRange() != null && !field.getReferenceRange().isBlank()) {
            score += 0.05;
        }
        if (line.contains(":") || line.contains(" ")) {
            score += 0.05;
        }
        return Math.min(1.0, score);
    }
}
