package com.ug.ug_inventory_management.dtos;

public class AdminResponseDTO {
    private Long id;
    private String name;

    public AdminResponseDTO() {
    }

    public AdminResponseDTO(Long id, String name) {
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
