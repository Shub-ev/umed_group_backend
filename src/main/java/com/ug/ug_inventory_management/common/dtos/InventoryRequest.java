package com.ug.ug_inventory_management.common.dtos;
import java.util.Map;
public class InventoryRequest {
    private Long templateId;
    private Long unitId;
    private Map<String, String> values;

    public Long getTemplateId() { return templateId; }

    public Long getUnitId() { return unitId; }

    public Map<String, String> getValues() { return values; }
}
