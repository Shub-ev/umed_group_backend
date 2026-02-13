package com.ug.ug_inventory_management.common.dtos;

public class AdminDTO {
    private Long id;
    private String name;

    public AdminDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    // Override to string to return custom object data string
    @Override
    public String toString() {
        return "Id : " + this.id + "\tName: " + name;
    }
}
