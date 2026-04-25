package online.umedgroup.ug_inventory_management.services;

import online.umedgroup.ug_inventory_management.common.dtos.CreateTemplateDTO;
import online.umedgroup.ug_inventory_management.common.exceptions.IllegalArgumentException;
import online.umedgroup.ug_inventory_management.enums.FieldType;
import online.umedgroup.ug_inventory_management.models.Template;
import online.umedgroup.ug_inventory_management.models.TemplateField;
import online.umedgroup.ug_inventory_management.repositories.TemplateFieldRepository;
import online.umedgroup.ug_inventory_management.repositories.TemplateRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        // 1. check template with same name
        if (templateRepository.existsByTemplateName(request.getTemplateName())) {
            throw new IllegalArgumentException("Template with same name already exists");
        }

        System.out.println("Fields: " + request.getFields());
        List<TemplateField> requestFields = request.getFields();

        List<String> fieldNames = requestFields.stream()
                .map(f -> f.getFieldName().trim().toUpperCase())
                .toList();

        // 2. check if mainField is present in fields
        String mainField = request.getMainField().trim().toUpperCase();
        if(!fieldNames.contains(mainField)) {
            throw new IllegalArgumentException("mainField must match one of the template fields");
        }

        // 3. Check duplicate fields from user
        if (fieldNames.size() != new java.util.HashSet<>(fieldNames).size()) {
            throw new IllegalArgumentException("Duplicate field names are not allowed");
        }

        // 4. Prevent adding fixed fields manually
        for (String name : fieldNames) {
            if (FIXED_FIELDS.contains(name)) {
                throw new IllegalArgumentException(name + " is a default field, no need to add it");
            }
        }

        // Save template
        Template template = new Template(request.getTemplateName(), mainField);
        templateRepository.save(template);

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
                field.setFieldType(FieldType.STRING);
            } else {
                field.setFieldType(FieldType.NUMBER);
            }

            field.setTemplate(template);
            field.setDisplayOrder(order++); // continues
            templateFieldRepository.save(field);
        }
    }


    @Transactional
    public void addFieldToTemplate(Long templateId, TemplateField fieldDTO) {

        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        String fieldName = fieldDTO.getFieldName().trim().toUpperCase();

        if (FIXED_FIELDS.contains(fieldName)) {
            throw new RuntimeException(fieldName + " is a default field");
        }

        boolean exists = templateFieldRepository
                .existsByTemplate_IdAndFieldNameIgnoreCase(templateId, fieldName);

        if (exists) {
            throw new RuntimeException("Field already exists");
        }

        // Get last display order (NO LOOP — optimized)
        Integer maxOrder = templateFieldRepository
                .findMaxDisplayOrder(templateId);

        int nextOrder = (maxOrder == null ? 1 : maxOrder + 1);

        TemplateField field = new TemplateField();
        field.setFieldName(fieldName);
        field.setFieldType(fieldDTO.getFieldType());
        field.setTemplate(template);
        field.setDisplayOrder(nextOrder);

        templateFieldRepository.save(field);
    }


    public Page<Template> getTemplates(String templateName, Pageable pageable) {

        if (templateName == null || templateName.trim().isEmpty()) {
            return templateRepository.findAll(pageable);
        }

        return templateRepository.findByTemplateNameContainingIgnoreCase(
                templateName.trim(),
                pageable
        );
    }

    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }

    public List<TemplateField> getFieldsByTemplateId(@NotNull Long templateId) {
        return templateFieldRepository.findByTemplate_IdOrderByDisplayOrderAsc(templateId);
    }
}