package online.umedgroup.ug_inventory_management.controllers;

import online.umedgroup.ug_inventory_management.common.dtos.CreateTemplateDTO;
import online.umedgroup.ug_inventory_management.common.dtos.UpdateTemplateDTO;
import online.umedgroup.ug_inventory_management.services.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import online.umedgroup.ug_inventory_management.models.TemplateField;
import java.util.List;
import online.umedgroup.ug_inventory_management.common.dtos.TemplateResponseDTO;

//@CrossOrigin(origins = "http://localhost:5173")
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

    @GetMapping("/employee/templates")
    public ResponseEntity<?> getEmployeeTemplates(
            @RequestParam Long employeeId,
            @RequestParam(required = false) String templateName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        log.info("Extracting templates accessible for Employee: {}", employeeId);
        return ResponseEntity.ok(
                templateService.getEmployeeTemplates(employeeId, templateName, page, size, sort)
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

    //delete API
    @DeleteMapping("/{templateId}")
    public ResponseEntity<?> deleteTemplate(@PathVariable Long templateId) {
        log.info("Deleting template with id: {}", templateId);
        templateService.deleteTemplate(templateId);
        log.info("Template deleted successfully!");

        return ResponseEntity.ok("Template deleted successfully");
    }

    @PatchMapping("/{templateId}/fields/{fieldId}")
    public ResponseEntity<?> renameField(
            @PathVariable Long templateId,
            @PathVariable Long fieldId,
            @RequestBody Map<String, String> request
    ) {
        templateService.renameTemplateField(
                templateId,
                fieldId,
                request.get("fieldName")
        );
        return ResponseEntity.ok("Field renamed successfully");
    }


    @PatchMapping("/{templateId}")
    public ResponseEntity<?> renameTemplate(
            @PathVariable Long templateId,
            @RequestBody Map<String, String> request
    ) {
        templateService.renameTemplate(
                templateId,
                request.get("templateName")
        );

        return ResponseEntity.ok("Template renamed successfully");
    }

    @PatchMapping("/{templateId}/access")
    public ResponseEntity<?> updateTemplateAccess(
            @PathVariable Long templateId,
            @RequestBody UpdateTemplateDTO request
    ) {
        log.info("Updating template access for templateId: {}", templateId);
        templateService.updateTemplate(templateId, request);
        log.info("Template access updated successfully!");
        return ResponseEntity.ok("Template access updated successfully");
    }

    @GetMapping("/{templateId}")
    public ResponseEntity<?> getTemplate(@PathVariable Long templateId) {
        return ResponseEntity.ok(templateService.getTemplateById(templateId));
    }
}