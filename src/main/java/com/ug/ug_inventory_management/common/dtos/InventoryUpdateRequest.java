
package com.ug.ug_inventory_management.common.dtos;
import com.ug.ug_inventory_management.enums.ActionType;
public class InventoryUpdateRequest {

    private Long templateId;
    private String unitName;
    private Long fieldId;
    private Long recordId;
    private int changeQty;
    private ActionType action; // INWARD / OUTWARD

    public Long getTemplateId() {
        return templateId;
    }


    public Long getFieldId() {
        return fieldId;
    }

    public int getChangeQty() {
        return changeQty;
    }

    public ActionType getAction() {
        return action;
    }

    public String getUnitName() {
        return unitName;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }


    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public void setChangeQty(int changeQty) {
        this.changeQty = changeQty;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }
}