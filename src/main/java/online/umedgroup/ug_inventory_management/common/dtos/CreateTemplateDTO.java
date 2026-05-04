package online.umedgroup.ug_inventory_management.common.dtos;
import jakarta.validation.constraints.NotNull;
import online.umedgroup.ug_inventory_management.models.TemplateField;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO for creating a template (dynamic tables)")
public class CreateTemplateDTO {

    @Schema(description = "Name of the template")
    @NotNull
    private String templateName;

    @Schema(description = "Name of main field")
    private String mainField;

    @Schema(description = "List of fields to add into the template")
    private List<TemplateField> templateFields;

    @Schema(description = "If template should be public or private")
    @NotNull
    private boolean isRestricted;

    @Schema(description = "List of employee IDs if template is private")
    private List<Long> employeeIds;


    // constructors
    public CreateTemplateDTO() {
    }

    public CreateTemplateDTO(String templateName, String mainField, List<TemplateField> templateFields, boolean isRestricted, List<Long> employeeIds) {
        this.templateName = templateName;
        this.mainField = mainField;
        this.templateFields = templateFields;
        this.isRestricted = isRestricted;
        this.employeeIds = employeeIds;
    }


    // Getters
    public String getTemplateName() {
        return templateName;
    }

    public List<TemplateField> getFields() {
        return templateFields;
    }

    public String getMainField() { return mainField; }

    public boolean isRestricted() { return isRestricted; }

    public List<Long> getEmployeeIds() { return employeeIds; }


    // Setters
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setFields(List<TemplateField> templateFields) {
        this.templateFields = templateFields;
    }

    public void setMainField(String mainField) { this.mainField = mainField; }

    public void setRestricted(boolean restricted) { isRestricted = restricted; }

    public void setEmployeeIds(List<Long> employeeIds) { this.employeeIds = employeeIds; }


    //toString()
    @Override
    public String toString() {
        return "CreateTemplateDTO{" +
                "templateName='" + templateName + '\'' +
                ", mainField='" + mainField + '\'' +
                ", templateFields=" + templateFields +
                ", isRestricted=" + isRestricted +
                ", employeeIds=" + employeeIds +
                '}';
    }
}