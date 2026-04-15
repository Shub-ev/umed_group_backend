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

    @ManyToOne
    @JoinColumn(name = "template_id")
    private Template template;

    private String unitName;

    @OneToMany(mappedBy = "inventoryRecord", cascade = CascadeType.ALL)
    private List<InventoryValue> values;

    public InventoryRecord() {
    }

    public InventoryRecord(Template template, String unitName) {
        this.template = template;
        this.unitName = unitName;
    }

    // Getters
    public Long getId() { return id; }

    public Template getTemplate() { return template; }

    public String getUnitName() { return unitName; }


    // Setters
    public void setTemplate(Template template) { this.template = template; }

    public void setUnitName(String unitName) { this.unitName = unitName; }


    // toString
    @Override
    public String toString() {
        return "InventoryRecord{" +
                "id=" + id +
                ", unitName='" + unitName + '\'' +
                '}';
    }
}