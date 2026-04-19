package com.ug.ug_inventory_management.controllers;

import com.ug.ug_inventory_management.common.dtos.CreateTemplateDTO;
import com.ug.ug_inventory_management.models.Template;
import com.ug.ug_inventory_management.services.TemplateService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.ug.ug_inventory_management.models.TemplateField;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService service) {
        this.templateService = service;
    }

    @PostMapping
    public Map<String, String> createTemplate(@RequestBody CreateTemplateDTO request) {
        templateService.createTemplate(request);
        return Map.of("message", "Template Created");
    }

    @GetMapping
    public List<Template> getAllTemplates() {
        return templateService.getAllTemplates();
    }

    @GetMapping("/{templateId}/fields")
    public List<TemplateField> getFieldsByTemplateId(@PathVariable Long templateId) {
        return templateService.getFieldsByTemplateId(templateId);
    }

    @PatchMapping("/{templateId}/add-field")
    public ResponseEntity<?> addField(
            @PathVariable Long templateId,
            @RequestBody TemplateField field
    ) {
        templateService.addFieldToTemplate(templateId, field);
        return ResponseEntity.ok("Field added successfully");
    }
}