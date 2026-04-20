package com.ug.ug_inventory_management.common.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "DTO for creating inventory record")
public class CreateInventoryRecordDTO {

    @Schema(description = "ID of template to which this record will belong")
    private Long templateId;

    private  Long eId;

    @Schema(description = "Name of the unit, to which this record will belong")
    private String unitName;

    @Schema(description = "Map of values to be stored in this record")
    private Map<String, String> values;

    public CreateInventoryRecordDTO() {
    }

    public CreateInventoryRecordDTO(Long templateId, String unitName, Map<String, String> values,Long eId) {
        this.templateId = templateId;
        this.unitName = unitName;
        this.values = values;
        this.eId=eId;
    }


    // Getters
    public Long getTemplateId() {
        return templateId;
    }

    public String getUnitName() {
        return unitName;
    }

    public Map<String, String> getValues() {
        return values;
    }


    public Long getEId() {
        return eId;
    }

    // Setters
    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public void setEId(Long eId) {
        this.eId = eId;
    }
}
