package com.ug.ug_inventory_management.models;

import jakarta.persistence.Column;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long eId;

    @Column(nullable = false)
    private String unitName;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false)
    private LocalDate allocation;

    protected Employee (){}

    public Employee(Long eId, String unitName,String password,LocalDate allocation){
        this.eId = eId;
        this.unitName = unitName;
        this.password = password;
        this.allocation = allocation;
    }

    // Getters
    public Long getId() { return id; }

    public Long getEId() {
        return eId;
    }

    public String getUnitName() {
        return unitName;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getAllocation() {
        return allocation;
    }


    // Setters
    public void setEId(Long eId) { this.eId = eId; }

    public void setAllocation(LocalDate allocation) {
        this.allocation = allocation;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", eId=" + eId +
                ", unitName='" + unitName + '\'' +
                ", allocation=" + allocation +
                '}';
    }
}
