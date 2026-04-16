
package com.ug.ug_inventory_management.common.dtos;
import com.ug.ug_inventory_management.enums.ActionType;
import io.swagger.v3.oas.annotations.media.Schema;

// ADD documentation comments
@Schema(description = "")
public class InventoryUpdateRecordDTO {

    private Long templateId;
    private Long recordId;
    private int changeQty;
    private ActionType action; // INWARD / OUTWARD
    private Long eId;
    private String unitName;


    public InventoryUpdateRecordDTO() {
    }

    public InventoryUpdateRecordDTO(Long templateId, Long recordId, int changeQty, ActionType action, Long eId, String unitName) {
        this.templateId = templateId;
        this.recordId = recordId;
        this.changeQty = changeQty;
        this.action = action;
        this.eId = eId;
        this.unitName = unitName;
    }


    public Long getTemplateId() { return templateId;}

    public int getChangeQty() {
        return changeQty;
    }

    public ActionType getAction() {
        return action;
    }

    public Long getRecordId() {
        return recordId;
    }

    public Long geteId() { return eId; }

    public String getUnitName() { return unitName;}



    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public void setChangeQty(int changeQty) {
        this.changeQty = changeQty;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }
}