package com.ug.ug_inventory_management.common.dtos;
import com.ug.ug_inventory_management.models.TemplateField;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO for creating a template (dynamic tables)")
public class CreateTemplateDTO {

    @Schema(description = "Name of the template")
    private String templateName;

    @Schema(description = "List of fields to add into the template")
    private List<TemplateField> templateFields;

    public CreateTemplateDTO() {
    }

    public CreateTemplateDTO(String templateName, List<TemplateField> templateFields) {
        this.templateName = templateName;
        this.templateFields = templateFields;
    }


    // Getters
    public String getTemplateName() {
        return templateName;
    }

    public List<TemplateField> getFields() {
        return templateFields;
    }


    // Setters
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setFields(List<TemplateField> templateFields) {
        this.templateFields = templateFields;
    }


    //toString()
    @Override
    public String toString() {
        return "CreateTemplateDTO{" +
                "templateName='" + templateName + '\'' +
                ", templateFields=" + templateFields +
                '}';
    }
}