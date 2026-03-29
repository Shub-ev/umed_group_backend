package com.ug.ug_inventory_management.common.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for responding admin data as response")
public class AdminResponseDTO {

    @Schema(description = "Admin id", example = "1")
    private Long id;

    @Schema(description = "Admin full name", example = "John Deo")
    private String name;

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

    // Override to string to return custom object data string
    @Override
    public String toString() {
        return "Id : " + this.id + "\tName: " + name;
    }
}
