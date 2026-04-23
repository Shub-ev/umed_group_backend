package online.umedgroup.ug_inventory_management.controllers;

import online.umedgroup.ug_inventory_management.common.dtos.CreateTemplateDTO;
import online.umedgroup.ug_inventory_management.services.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import online.umedgroup.ug_inventory_management.models.TemplateField;
import java.util.List;

@RestController
@RequestMapping("/templates")
public class TemplateController {

    private final TemplateService templateService;
    private final Logger log = LoggerFactory.getLogger(TemplateController.class);

    public TemplateController(TemplateService service) {
        this.templateService = service;
    }

    @PostMapping
    public ResponseEntity<?> createTemplate(@RequestBody CreateTemplateDTO request) {
        log.info("Creating new template: {}", request);
        templateService.createTemplate(request);
        log.info("Template created!");
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getTemplates(
            @RequestParam(required = false) String templateName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        String sortDirection = (sortParams.length > 1) ? sortParams[1] : "asc";

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortField)
        );

        return ResponseEntity.ok(
                templateService.getTemplates(templateName, pageable)
        );
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