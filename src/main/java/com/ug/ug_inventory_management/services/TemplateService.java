package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.common.dtos.*;
import com.ug.ug_inventory_management.enums.FieldType;
import com.ug.ug_inventory_management.models.*;
import com.ug.ug_inventory_management.repositories.*;
import com.ug.ug_inventory_management.utils.TemplateField;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List; // ✅ ADD THIS

@Service
public class TemplateService {

    private final TemplateRepository templateRepo;
    private final TemplateFieldRepository fieldRepo;

    public TemplateService(TemplateRepository t, TemplateFieldRepository f) {
        this.templateRepo = t;
        this.fieldRepo = f;
    }

    @Transactional
    public void createTemplate(@NotNull CreateTemplateDTO request) {

        if(templateRepo.existsByTemplateName(request.getTemplateName())) {
            throw new RuntimeException("Template already exists");
        }

        Template template = new Template(request.getTemplateName());
        templateRepo.save(template);

        System.out.println("Fields: " + request.getFields());

        for(TemplateField f : request.getFields()) {
            System.out.println(f);

            com.ug.ug_inventory_management.models.TemplateField templateField = new com.ug.ug_inventory_management.models.TemplateField();
            templateField.setTemplate(template);
            templateField.setFieldName(f.getName());
            templateField.setFieldType(FieldType.valueOf(f.getType()));

            fieldRepo.save(templateField);
        }
    }

    public List<Template> getAllTemplates() {
        return templateRepo.findAll();
    }
}