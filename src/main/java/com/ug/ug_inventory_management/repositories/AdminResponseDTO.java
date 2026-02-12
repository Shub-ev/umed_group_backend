package com.ug.ug_inventory_management.dtos;

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
}
