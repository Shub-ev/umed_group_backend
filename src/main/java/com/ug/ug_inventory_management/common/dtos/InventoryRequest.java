package com.ug.ug_inventory_management.common.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "DTO for creating inventory record")
public class InventoryRequest {

    @Schema(description = "ID of template to which this record will belong")
    private Long templateId;

    @Schema(description = "Name of the unit, to which this record will belong")
    private String unitName;

    @Schema(description = "Map of values to be stored in this record")
    private Map<String, String> values;

    public InventoryRequest() {
    }

    public InventoryRequest(Long templateId, String unitName, Map<String, String> values) {
        this.templateId = templateId;
        this.unitName = unitName;
        this.values = values;
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
}
