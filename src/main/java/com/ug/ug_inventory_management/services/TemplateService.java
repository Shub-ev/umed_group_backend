package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.common.dtos.CreateTemplateDTO;
import com.ug.ug_inventory_management.models.Template;
import com.ug.ug_inventory_management.models.TemplateField;
import com.ug.ug_inventory_management.repositories.TemplateFieldRepository;
import com.ug.ug_inventory_management.repositories.TemplateRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List; // ✅ ADD THIS

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateFieldRepository templateFieldRepository;

    public TemplateService(TemplateRepository templateRepository, TemplateFieldRepository templateFieldRepository) {
        this.templateRepository = templateRepository;
        this.templateFieldRepository = templateFieldRepository;
    }


@Transactional
public void createTemplate(@NotNull CreateTemplateDTO request) {

    if (templateRepository.existsByTemplateName(request.getTemplateName())) {
        throw new RuntimeException("Template already exists");
    }

    Template template = new Template(request.getTemplateName());
    templateRepository.save(template);

    System.out.println("Fields: " + request.getFields());

    for (TemplateField fieldDTO : request.getFields()) {

        TemplateField field = new TemplateField();

        // 🔥 FIX 1: map correct field
        field.setFieldName(fieldDTO.getFieldName());  // or getFieldName() depending on DTO

        // 🔥 FIX 2: map type
        field.setFieldType(fieldDTO.getFieldType());

        // 🔥 FIX 3: set relationship
        field.setTemplate(template);

        templateFieldRepository.save(field);
    }
}

    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }

    public List<TemplateField> getFieldsByTemplateId(@NotNull Long templateId) {
        return templateFieldRepository.findByTemplate_Id(templateId);
    }
}