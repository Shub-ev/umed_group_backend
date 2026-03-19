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

@Entity
@Table(name = "inventory_values")
public class InventoryValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recordId;
    private Long fieldId;

    @Column(columnDefinition = "TEXT")
    private String value;

    // ✅ GETTERS
    public Long getId() {
        return id;
    }

    public Long getRecordId() {
        return recordId;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public String getValue() {
        return value;
    }

    // ✅ SETTERS
    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public void setValue(String value) {
        this.value = value;
    }
}