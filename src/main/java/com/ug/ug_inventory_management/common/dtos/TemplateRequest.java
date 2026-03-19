package com.ug.ug_inventory_management.common.dtos;
import java.util.Map;
import java.util.List;

public class TemplateRequest {

    private String templateName;
    private List<FieldRequest> fields;

    public String getTemplateName() { return templateName; }

    public List<FieldRequest> getFields() { return fields; }

    public static class InventoryRequest {

        private Long templateId;
        private Long unitId;
        private Map<String, String> values;

        public Long getTemplateId() { return templateId; }

        public Long getUnitId() { return unitId; }

        public Map<String, String> getValues() { return values; }
    }
}