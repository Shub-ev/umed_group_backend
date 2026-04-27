package online.umedgroup.ug_inventory_management.common.dtos;

import java.time.LocalDateTime;

public class ReportRequestDTO {

    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String type;
    private String unit;
    private Long templateId;
    private  String templateName;
    private String mainField;

    // getters & setters
    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDateTime getToDate() {
        return toDate;
    }

    public void setToDate(LocalDateTime toDate) {
        this.toDate = toDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
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
}