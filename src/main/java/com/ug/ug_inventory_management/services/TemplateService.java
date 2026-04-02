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
    private static final List<String> FIXED_FIELDS =
            List.of("INWARD", "OUTWARD", "STOCK", "BY");

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

        // ✅ Define fixed fields

        System.out.println("Fields: " + request.getFields());

        // ✅ Normalize user input
        List<TemplateField> requestFields = request.getFields();

        List<String> fieldNames = requestFields.stream()
                .map(f -> f.getFieldName().trim().toUpperCase())
                .toList();

        // ✅ 1. Check duplicate fields from user
        if (fieldNames.size() != new java.util.HashSet<>(fieldNames).size()) {
            throw new RuntimeException("Duplicate field names are not allowed");
        }

        // ✅ 2. Prevent adding fixed fields manually
        for (String name : fieldNames) {
            if (FIXED_FIELDS.contains(name)) {
                throw new RuntimeException(name + " is a default field, no need to add it");
            }
        }

        // ✅ 3. Save USER fields
        int order = 1;
          // ✅ 1. Save USER fields FIRST
        for (TemplateField fieldDTO : requestFields) {
            TemplateField field = new TemplateField();
            field.setFieldName(fieldDTO.getFieldName().trim().toUpperCase());
            field.setFieldType(fieldDTO.getFieldType());
            field.setTemplate(template);
            field.setDisplayOrder(order++); // 🔥 KEY
            templateFieldRepository.save(field);
        }

        // ✅ 2. Save FIXED fields LAST
        for (String fixed : FIXED_FIELDS) {

            TemplateField field = new TemplateField();

            field.setFieldName(fixed);

            if (fixed.equals("BY")) {
                field.setFieldType(com.ug.ug_inventory_management.enums.FieldType.STRING);
            } else {
                field.setFieldType(com.ug.ug_inventory_management.enums.FieldType.NUMBER);
            }

            field.setTemplate(template);

            field.setDisplayOrder(order++); // continues

            templateFieldRepository.save(field);
        }

    }

    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }

    public List<TemplateField> getFieldsByTemplateId(@NotNull Long templateId) {
        return templateFieldRepository.findByTemplate_IdOrderByDisplayOrderAsc(templateId);
    }
}