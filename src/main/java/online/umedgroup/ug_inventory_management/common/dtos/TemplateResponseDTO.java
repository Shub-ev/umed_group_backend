package online.umedgroup.ug_inventory_management.common.dtos;

import online.umedgroup.ug_inventory_management.models.Employee;

import java.util.List;

public class TemplateResponseDTO {
    private Long id;
    private String templateName;
    private String mainField;
    private boolean restricted;
    private List<Employee> employees;

    // Default constructor
    public TemplateResponseDTO() {}

    // Constructor
    public TemplateResponseDTO(Long id, String templateName, String mainField, boolean restricted, List<Employee> employees) {
        this.id = id;
        this.templateName = templateName;
        this.mainField = mainField;
        this.restricted = restricted;
        this.employees = employees;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getMainField() {
        return mainField;
    }

    public void setMainField(String mainField) {
        this.mainField = mainField;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}
