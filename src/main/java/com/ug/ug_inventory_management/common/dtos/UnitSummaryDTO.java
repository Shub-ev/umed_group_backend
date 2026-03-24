package com.ug.ug_inventory_management.common.dtos;

import java.util.Map;

public class UnitSummaryDTO {

    private Long unitId;
    private Map<String, Integer> values;

    public UnitSummaryDTO(Long unitId, Map<String, Integer> values) {
        this.unitId = unitId;
        this.values = values;
    }

    public Long getUnitId() {
        return unitId;
    }

    public Map<String, Integer> getValues() {
        return values;
    }
}