package com.ug.ug_inventory_management.models;

import jakarta.persistence.*;

import java.util.List;

/*
 * Inventory Record
 * This stores each record as row of table.
 * Each inventory record with corresponding to template, is as row to the table.
 */
@Entity
@Table(name = "inventory_records")
public class InventoryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long template_id;

    private String unit_name;

    @OneToMany(mappedBy = "inventory_record", cascade = CascadeType.ALL)
    private List<InventoryValue> values;

    public InventoryRecord() {
    }

    public InventoryRecord(Long template_id, String unit_name) {
        this.template_id = template_id;
        this.unit_name = unit_name;
    }

    // Getters
    public Long getId() { return id; }

    public Long getTemplateId() { return template_id; }

    public String getUnitName() { return unit_name; }


    // Setters
    public void setTemplateId(Long template_id) { this.template_id = template_id; }

    public void setUnitName(String unit_name) { this.unit_name = unit_name; }
}