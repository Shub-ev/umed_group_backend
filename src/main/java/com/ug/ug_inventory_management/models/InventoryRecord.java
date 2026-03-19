package com.ug.ug_inventory_management.models;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory_records")
public class InventoryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long templateId;

    private Long unitId;

    public Long getId() { return id; }

    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public void setUnitId(Long unitId) { this.unitId = unitId; }
}