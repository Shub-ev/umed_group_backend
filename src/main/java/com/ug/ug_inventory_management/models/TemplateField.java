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

    @ManyToOne
    @JoinColumn(name = "template_id")
    private Template template;

    @Column(nullable = false)
    private String fieldName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FieldType fieldType;


    public TemplateField() {
    }

    public TemplateField(Template template, String fieldName, FieldType fieldType) {
        this.template = template;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    // Getters
    public Long getId() { return id; }

    public Template getTemplate() { return template; }

    public String getFieldName() { return fieldName; }

    public FieldType getFieldType() { return fieldType; }


    // Setters
    public void setTemplate(Template template) { this.template = template; }

    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public void setFieldType(FieldType fieldType) { this.fieldType = fieldType; }


    // toString
    @Override
    public String toString() {
        return "TemplateField{" +
                "id=" + id +
                ", template=" + template +
                ", fieldName='" + fieldName + '\'' +
                ", fieldType=" + fieldType +
                '}';
    }
}