package com.ug.ug_inventory_management.models;

import jakarta.persistence.*;

@Entity
@Table(name = "templates")
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String templateName;

    public Long getId() { return id; }

    public String getTemplateName() { return templateName; }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}