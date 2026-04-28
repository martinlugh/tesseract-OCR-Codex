package com.example.ocrcloud.service.extract;

import com.example.ocrcloud.model.template.ReportTemplate;
import org.springframework.stereotype.Service;

@Service
public class TemplateMatchServiceImpl implements TemplateMatchService {

    private final ReportTemplateLoader reportTemplateLoader;

    public TemplateMatchServiceImpl(ReportTemplateLoader reportTemplateLoader) {
        this.reportTemplateLoader = reportTemplateLoader;
    }

    @Override
    public ReportTemplate match(String cleanedText) {
        ReportTemplate best = null;
        int bestScore = -1;
        for (ReportTemplate template : reportTemplateLoader.getTemplates()) {
            int score = 0;
            for (String keyword : template.getMatchKeywords()) {
                if (cleanedText.contains(keyword)) {
                    score++;
                }
            }
            if (score > bestScore) {
                best = template;
                bestScore = score;
            }
        }
        return best;
    }
}
