package online.umedgroup.ug_inventory_management.services;

import online.umedgroup.ug_inventory_management.common.dtos.CreateTemplateDTO;
import online.umedgroup.ug_inventory_management.common.dtos.UpdateTemplateDTO;
import online.umedgroup.ug_inventory_management.common.dtos.TemplateResponseDTO;
import online.umedgroup.ug_inventory_management.common.exceptions.IllegalArgumentException;
import online.umedgroup.ug_inventory_management.enums.FieldType;
import online.umedgroup.ug_inventory_management.models.Employee;
import online.umedgroup.ug_inventory_management.models.Template;
import online.umedgroup.ug_inventory_management.models.TemplateField;
import online.umedgroup.ug_inventory_management.repositories.*;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List; // ✅ ADD THIS

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateFieldRepository templateFieldRepository;
    private final InventoryRecordRepository inventoryRecordRepository;
    private final InventoryValueRepository inventoryValueRepository;
    private static final List<String> FIXED_FIELDS =
            List.of("INWARD", "OUTWARD", "STOCK", "BY");
    private final EmployeeRepository employeeRepository;

    public TemplateService(TemplateRepository templateRepository, TemplateFieldRepository templateFieldRepository, InventoryRecordRepository inventoryRecordRepository,
                           InventoryValueRepository inventoryValueRepository, EmployeeRepository employeeRepository) {
        this.templateRepository = templateRepository;
        this.templateFieldRepository = templateFieldRepository;
        this.inventoryRecordRepository = inventoryRecordRepository;
        this.inventoryValueRepository = inventoryValueRepository;
        this.employeeRepository = employeeRepository;
    }


    @Transactional
    public void createTemplate(@NotNull CreateTemplateDTO request) {

        // 1. check template with same name
        if (templateRepository.existsByTemplateName(request.getTemplateName())) {
            throw new IllegalArgumentException("Template with same name already exists");
        }

        List<TemplateField> requestFields = request.getFields();

        List<String> fieldNames = requestFields.stream()
                .map(f -> f.getFieldName().trim().toUpperCase())
                .toList();

        // 2. check if mainField is present in fields
        if(request.getMainField() == null) {
            throw new IllegalArgumentException("Main field cannot be empty!");
        }
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

        // 5. Check isRestricted and if corresponding employees are present
        if(request.isRestricted() && (request.getEmployeeIds() == null ||
            request.getEmployeeIds().isEmpty())) {
            throw new IllegalArgumentException("Restricted template must have at least one employee");
        }

        // 6. Extract employees for restricted templates
        List<Employee> employees = employeeRepository.findAllById(request.getEmployeeIds());
        if(employees.size() != request.getEmployeeIds().size()) {
            throw new IllegalArgumentException("Some employee IDs are invalid");
        }

        // Save template
        Template template = new Template(request.getTemplateName(), mainField, request.isRestricted());
        template.setEmployees(employees);
        templateRepository.save(template);

        // 7. Save USER fields
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
    public void renameTemplateField(Long templateId, Long fieldId, String newFieldName) {

        if (newFieldName == null || newFieldName.trim().isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be empty");
        }

        String normalizedNewName = newFieldName.trim().toUpperCase();

        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        TemplateField field = templateFieldRepository.findById(fieldId)
                .orElseThrow(() -> new RuntimeException("Field not found"));

        if (!field.getTemplate().getId().equals(templateId)) {
            throw new RuntimeException("Field does not belong to this template");
        }

        String oldName = field.getFieldName().trim().toUpperCase();

        if (FIXED_FIELDS.contains(oldName)) {
            throw new RuntimeException(oldName + " is a default field and cannot be renamed");
        }

        if (FIXED_FIELDS.contains(normalizedNewName)) {
            throw new RuntimeException(normalizedNewName + " is a default field name");
        }

        // allow same field to keep same name, but block duplicates from other fields
        templateFieldRepository.findByTemplate_IdAndFieldNameIgnoreCase(templateId, normalizedNewName)
                .ifPresent(existing -> {
                    if (!existing.getId().equals(fieldId)) {
                        throw new RuntimeException("Field name already exists");
                    }
                });

        field.setFieldName(normalizedNewName);
        templateFieldRepository.save(field);

        // keep templates.mainField in sync
        if (template.getMainField() != null &&
                template.getMainField().trim().equalsIgnoreCase(oldName)) {
            template.setMainField(normalizedNewName);
            templateRepository.save(template);
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


    // delete template
    @Transactional
    public void deleteTemplate(Long templateId) {

        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        inventoryValueRepository.deleteByTemplateId(templateId);
        inventoryRecordRepository.deleteByTemplateId(templateId);
        templateFieldRepository.deleteByTemplateId(templateId);

        templateRepository.delete(template);
    }


    //rename template
    @Transactional
    public void renameTemplate(Long templateId, String newName) {

        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Template name cannot be empty");
        }

        String normalizedName = newName.trim().toUpperCase();

        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        // prevent duplicate names
        if (!template.getTemplateName().equalsIgnoreCase(normalizedName) &&
                templateRepository.existsByTemplateName(normalizedName)) {
            throw new RuntimeException("Template name already exists");
        }

        template.setTemplateName(normalizedName);
        templateRepository.save(template);
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

    public Page<Template> getEmployeeTemplates(
            Long employeeId,
            String templateName,
            int page,
            int size,
            String sort
    ) {

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 &&
                sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        if (templateName == null || templateName.trim().isEmpty()) {
            return templateRepository.findEmployeeAccessibleTemplates(employeeId, pageable);
        }

        return templateRepository.findEmployeeAccessibleTemplatesByName(
                employeeId,
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

    @Transactional
    public void updateTemplate(Long templateId, UpdateTemplateDTO request) {
        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        // Check isRestricted and if corresponding employees are present
        if (request.isRestricted() && (request.getEmployeeIds() == null ||
                request.getEmployeeIds().isEmpty())) {
            throw new IllegalArgumentException("Restricted template must have at least one employee");
        }

        // Extract employees for restricted templates
        List<Employee> employees = List.of();
        if (request.isRestricted()) {
            employees = employeeRepository.findAllById(request.getEmployeeIds());
            if (employees.size() != request.getEmployeeIds().size()) {
                throw new IllegalArgumentException("Some employee IDs are invalid");
            }
        }

        // Update template
        template.setRestricted(request.isRestricted());
        template.setEmployees(employees);
        templateRepository.save(template);
    }

    public TemplateResponseDTO getTemplateById(@NotNull Long templateId) {
        Template template = templateRepository.findByIdWithEmployees(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        return new TemplateResponseDTO(
                template.getId(),
                template.getTemplateName(),
                template.getMainField(),
                template.isRestricted(),
                template.getEmployees()
        );
    }
}
