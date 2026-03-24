package com.ug.ug_inventory_management.controllers;

import com.ug.ug_inventory_management.common.dtos.CreateTemplateDTO;
import com.ug.ug_inventory_management.models.Template;
import com.ug.ug_inventory_management.services.TemplateService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/templates")
public class TemplateController {

    private final TemplateService service;

    public TemplateController(TemplateService service) {
        this.service = service;
    }

    @PostMapping
    public Map<String, String> createTemplate(@RequestBody CreateTemplateDTO request) {
        service.createTemplate(request);
        return Map.of("message", "Template Created");
    }

    @GetMapping
    public List<Template> getAllTemplates() {
        return service.getAllTemplates();
    }
}