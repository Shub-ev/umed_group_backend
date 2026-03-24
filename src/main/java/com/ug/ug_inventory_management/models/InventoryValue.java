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
    @JoinColumn(name = "record_id")
    private InventoryRecord inventoryRecord;

    private Long fieldId;

    @Column(columnDefinition = "TEXT")
    private String value;

    public InventoryValue() {
    }

    public InventoryValue(InventoryRecord inventoryRecord, Long fieldId, String value) {
        this.inventoryRecord = inventoryRecord;
        this.fieldId = fieldId;
        this.value = value;
    }

    // ✅ GETTERS
    public Long getId() {
        return id;
    }

    public InventoryRecord getInventoryRecord() {
        return inventoryRecord;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public String getValue() {
        return value;
    }


    // ✅ SETTERS
    public void setInventoryRecord(InventoryRecord inventoryRecord) {
        this.inventoryRecord = inventoryRecord;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public void setValue(String value) {
        this.value = value;
    }
}