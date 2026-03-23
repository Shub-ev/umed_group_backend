package com.ug.ug_inventory_management.models;

import com.ug.ug_inventory_management.enums.FieldType;
import jakarta.persistence.*;

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

    public Long getId() { return id; }

    public Long getTemplateId() { return templateId; }

    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public String getFieldName() { return fieldName; }

    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public FieldType getFieldType() { return fieldType; }

    public void setFieldType(FieldType fieldType) { this.fieldType = fieldType; }
}