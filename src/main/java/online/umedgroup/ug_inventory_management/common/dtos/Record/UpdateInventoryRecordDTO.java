
package online.umedgroup.ug_inventory_management.common.dtos.Record;
import online.umedgroup.ug_inventory_management.enums.ActionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

// ADD documentation comments
@Schema(description = "")
public class UpdateInventoryRecordDTO {

    private Long templateId;
    private String templateName;
    private Long recordId;
    private int changeQty;
    private ActionType action; // INWARD / OUTWARD
    private Long eId;
    private String unitName;
    private Map<String, String> values;


    public UpdateInventoryRecordDTO() {
    }

    public UpdateInventoryRecordDTO(Long templateId, Long recordId, int changeQty, ActionType action, Long eId, String unitName, String templateName, Map<String, String> values) {
        this.templateId = templateId;
        this.recordId = recordId;
        this.changeQty = changeQty;
        this.action = action;
        this.eId = eId;
        this.unitName = unitName;
        this.templateName=templateName;
        this.values = values;
    }


    // getters
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

    public Long getEId() { return eId; }

    public String getUnitName() { return unitName;}

    public String getTemplateName() {
        return templateName;
    }

    public Map<String, String> getValues() { return values; }


    // setters
    public void setTemplatEId(Long templateId) {
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

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setValues(Map<String, String> values) { this.values = values; }


    // toStirng()
    @Override
    public String toString() {
        return "UpdateInventoryRecordDTO{" +
                "templateId=" + templateId +
                ", templateName='" + templateName + '\'' +
                ", recordId=" + recordId +
                ", changeQty=" + changeQty +
                ", action=" + action +
                ", eId=" + eId +
                ", unitName='" + unitName + '\'' +
                ", values=" + values +
                '}';
    }
}