package com.ug.ug_inventory_management.common.dtos;

import java.util.Map;

public class UnitSummaryDTO {

    private String unitName;
    private Map<String, Integer> values;

    public UnitSummaryDTO(String unitName, Map<String, Integer> values) {
        this.unitName = unitName;
        this.values = values;
    }

    public String getUnitName() {
        return unitName;
    }

    public Map<String, Integer> getValues() {
        return values;
    }
}