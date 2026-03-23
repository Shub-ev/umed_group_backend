package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.common.dtos.*;
import com.ug.ug_inventory_management.models.*;
import com.ug.ug_inventory_management.repositories.*;
import jakarta.transaction.Transactional;
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
    public void createTemplate(TemplateRequest request) {

        if(templateRepo.existsByTemplateName(request.getTemplateName())) {
            throw new RuntimeException("Template already exists");
        }

        Template template = new Template();
        template.setTemplateName(request.getTemplateName());
        templateRepo.save(template);

        System.out.println("Fields: " + request.getFields());

        for(FieldRequest f : request.getFields()) {
            System.out.println(f);

            TemplateField field = new TemplateField();
            field.setTemplateId(template.getId());
            field.setFieldName(f.getName());
            field.setFieldType(FieldType.valueOf(f.getType()));

            fieldRepo.save(field);
        }
    }

    public List<Template> getAllTemplates() {
        return templateRepo.findAll();
    }
}