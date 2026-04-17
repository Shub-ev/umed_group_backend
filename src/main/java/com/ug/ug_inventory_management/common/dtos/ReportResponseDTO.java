package com.ug.ug_inventory_management.common.dtos;

public class ReportResponseDTO {

    private String unit;
    private Long templateId;
    private String templateName;

    private Long totalInward;
    private Long totalOutward;
    private Long stock;

    // getters & setters

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

    public Long getTotalInward() {
        return totalInward;
    }

    public void setTotalInward(Long totalInward) {
        this.totalInward = totalInward;
    }

    public Long getTotalOutward() {
        return totalOutward;
    }

    public void setTotalOutward(Long totalOutward) {
        this.totalOutward = totalOutward;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }
}