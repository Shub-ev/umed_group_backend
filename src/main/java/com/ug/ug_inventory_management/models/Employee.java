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
    private Long eid;

    @Column(nullable = false)
    private String unit_name;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false)
    private LocalDate allocation;

    protected Employee (){}

    public Employee( String unit_name,String password,LocalDate allocation){
        this.unit_name = unit_name;
        this.password = password;
        this.allocation = allocation;
    }

    public Long getEid() {
        return eid;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getAllocation() {
        return allocation;
    }

    public void setAllocation(LocalDate allocation) {
        this.allocation = allocation;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
