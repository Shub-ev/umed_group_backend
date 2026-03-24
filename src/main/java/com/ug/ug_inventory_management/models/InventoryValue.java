//package com.ug.ug_inventory_management.models;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "inventory_values")
//public class InventoryValue {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private Long recordId;
//
//    private Long fieldId;
//
//    @Column(columnDefinition = "TEXT")
//    private String value;
//
//    public void setRecordId(Long recordId) { this.recordId = recordId; }
//
//    public void setFieldId(Long fieldId) { this.fieldId = fieldId; }
//
//    public void setValue(String value) { this.value = value; }
//}

package com.ug.ug_inventory_management.models;

import jakarta.persistence.*;

/*
 * Inventory Value
 * Acts as field for Inventory record.
 * Inventory record can have multiple corresponding Inventory fields.
 */
@Entity
@Table(name = "inventory_values")
public class InventoryValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "record")
    private InventoryRecord inventory_record;

    private Long fieldId;

    @Column(columnDefinition = "TEXT")
    private String value;

    public InventoryValue() {
    }

    public InventoryValue(InventoryRecord inventory_record, Long fieldId, String value) {
        this.inventory_record = inventory_record;
        this.fieldId = fieldId;
        this.value = value;
    }

    // ✅ GETTERS
    public Long getId() {
        return id;
    }

    public InventoryRecord getRecordId() {
        return inventory_record;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public String getValue() {
        return value;
    }


    // ✅ SETTERS
    public void setRecordId(InventoryRecord inventory_record) {
        this.inventory_record = inventory_record;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public void setValue(String value) {
        this.value = value;
    }
}