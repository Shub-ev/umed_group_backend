package com.ug.ug_inventory_management.common.dtos;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO for creating a template (dynamic tables)")
public class CreateTemplateDTO {

    @Schema(description = "Name of the template")
    private String templateName;

    @Schema(description = "List of fields to add into the template")
    private List<FieldRequest> fields;

    public CreateTemplateDTO() {
    }

    public CreateTemplateDTO(String templateName, List<FieldRequest> fields) {
        this.templateName = templateName;
        this.fields = fields;
    }


    // Getters
    public String getTemplateName() {
        return templateName;
    }

    public List<FieldRequest> getFields() {
        return fields;
    }


    // Setters
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setFields(List<FieldRequest> fields) {
        this.fields = fields;
    }
}