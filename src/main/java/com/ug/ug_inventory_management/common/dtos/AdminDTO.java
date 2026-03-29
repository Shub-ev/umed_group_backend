package com.ug.ug_inventory_management.common.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for creating and login admin.")
public class AdminDTO {

    @Schema(description = "Admin ID", example = "1234")
    private Long Id;

    @Schema(description = "Admin name", example = "John Deo")
    private String name;

    @Schema(hidden = true)
    private String password;

    public AdminDTO() {
    }

    public AdminDTO(String name,String password) {
        this.name=name;
        this.password=password;
    }


    public Long getId() { return Id; }
    public String getName() {
        return name;
    }
    public String getPassword() {
        return password;
    }

    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
}
