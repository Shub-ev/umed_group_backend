package com.ug.ug_inventory_management.common.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class AdminDTO {

    private Long id;

    private String name;

    private String password;

    public AdminDTO() {
    }

    public AdminDTO(String name,String password) {
        this.name=name;
        this.password=password;
    }

    public Long getId() { return id; }
    public String getName() {
        return name;
    }
    public String getPassword() {
        return password;
    }

    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
}
