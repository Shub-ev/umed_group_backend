package com.ug.ug_inventory_management.models;

import jakarta.persistence.*;

@Entity
@Table(name = "unit_names")
public class UnitName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long unitId;

    @Column(nullable = false, unique = true)
    private String unitName;


    /* Constructors */
    public UnitName() {
    }
    public UnitName(String unitName){
        this.unitName = unitName;
    }
    public UnitName(Long unitId, String unitName) {
        this.unitId = unitId;
        this.unitName = unitName;
    }


    /* Getters */
    public Long getUnitId() {
        return unitId;
    }
    public String getUnitName() {
        return unitName;
    }


    /* Setters */
    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }


    /* Override toString() */
    @Override
    public String toString() {
        return "UnitName{" +
                "unitId=" + unitId +
                ", unitName='" + unitName + '\'' +
                '}';
    }
}
