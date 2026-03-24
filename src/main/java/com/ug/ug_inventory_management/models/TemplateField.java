package com.ug.ug_inventory_management.models;

import com.ug.ug_inventory_management.enums.FieldType;
import jakarta.persistence.*;

/*
 * Template Fields
 * This creates pool of fields for different templates.
 * Each field acts as column of table.
 */
@Entity
@Table(name = "template_fields")
public class TemplateField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long templateId;

    private String fieldName;

    @Enumerated(EnumType.STRING)
    private FieldType fieldType;


    public TemplateField() {
    }

    public TemplateField(Long templateId, String fieldName, FieldType fieldType) {
        this.templateId = templateId;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    public Long getId() { return id; }

    public Long getTemplateId() { return templateId; }

    public String getFieldName() { return fieldName; }

    public FieldType getFieldType() { return fieldType; }

    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public void setFieldType(FieldType fieldType) { this.fieldType = fieldType; }
}