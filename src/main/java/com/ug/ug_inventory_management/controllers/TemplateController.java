package com.ug.ug_inventory_management.controllers;

import com.ug.ug_inventory_management.common.dtos.TemplateRequest;
import com.ug.ug_inventory_management.models.Template;
import com.ug.ug_inventory_management.services.TemplateService;
import org.springframework.web.bind.annotation.*;
import com.ug.ug_inventory_management.models.TemplateField;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService service;

    public TemplateController(TemplateService service) {
        this.service = service;
    }

    @PostMapping
    public Map<String, String> createTemplate(@RequestBody TemplateRequest request) {
        service.createTemplate(request);
        return Map.of("message", "Template Created");
    }

    @GetMapping
    public List<Template> getAllTemplates() {
        return service.getAllTemplates();
    }

    @GetMapping("/{templateId}/fields")
    public List<TemplateField> getFieldsByTemplateId(@PathVariable Long templateId) {
        return service.getFieldsByTemplateId(templateId);
    }
}