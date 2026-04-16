package com.ug.ug_inventory_management.common.dtos.Admin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for responding admin data along with JWT token as response")
public class AdminLoginResponseDTO {

    @Schema(description = "Admin id", example = "1")
    private Long id;

    @Schema(description = "Admin full name", example = "John Deo")
    private String name;

    @Schema(description = "JWT token")
    private String token;

    public AdminLoginResponseDTO(Long id, String name, String token) {
        this.id = id;
        this.name = name;
        this.token = token;
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getToken() { return token; }

    // Override to string to return custom object data string
    @Override
    public String toString() {
        return "AdminLoginResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
