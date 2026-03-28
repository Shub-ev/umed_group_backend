package com.ug.ug_inventory_management.common.dtos;

public class InventoryUpdateRequest {

    private Long templateId;
    private Long unitId;
    private Long fieldId;
    private int changeQty;
    private String action; // INWARD / OUTWARD

    // Getters & Setters

    public Long getTemplateId() {
        return templateId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public int getChangeQty() {
        return changeQty;
    }

    public String getAction() {
        return action;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public void setChangeQty(int changeQty) {
        this.changeQty = changeQty;
    }

    public void setAction(String action) {
        this.action = action;
    }
}