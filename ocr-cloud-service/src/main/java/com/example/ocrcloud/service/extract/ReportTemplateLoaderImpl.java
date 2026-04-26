package com.example.ocrcloud.service.extract;

import com.example.ocrcloud.model.template.ReportTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportTemplateLoaderImpl implements ReportTemplateLoader {

    private final List<ReportTemplate> templates;

    public ReportTemplateLoaderImpl() {
        this.templates = loadTemplates();
    }

    @Override
    public List<ReportTemplate> getTemplates() {
        return templates;
    }

    private List<ReportTemplate> loadTemplates() {
        List<ReportTemplate> list = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath*:templates/*.yml");
            for (Resource resource : resources) {
                try (InputStream in = resource.getInputStream()) {
                    ReportTemplate template = mapper.readValue(in, ReportTemplate.class);
                    list.add(template);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("加载模板失败", e);
        }
        return list;
    }
}
