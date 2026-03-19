package com.ug.ug_inventory_management.controllers;

import com.ug.ug_inventory_management.common.dtos.TemplateRequest;
import com.ug.ug_inventory_management.services.TemplateService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService service;

    public TemplateController(TemplateService service) {
        this.service = service;
    }

    @PostMapping
    public String createTemplate(@RequestBody TemplateRequest request) {
        service.createTemplate(request);
        return "Template Created";
    }
}