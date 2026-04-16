package com.ug.ug_inventory_management.common.dtos.Admin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for updating admin password")
public class AdminPasswordUpdateDTO {

    @Schema(description = "Admin id", example = "1")
    private Long id;

    @Schema(description = "Admin name", example = "John Deo")
    private String name;

    @Schema(hidden = true)
    private String passwordPre;
    @Schema(hidden = true)
    private String passwordNew;

    //default constructor
    public AdminPasswordUpdateDTO() {}

    public AdminPasswordUpdateDTO(Long id, String password, String password1, String name) {
        this.id = id;
        this.passwordPre = password;
        this.passwordNew = password1;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPasswordPre() {
        return passwordPre;
    }

    public String getPasswordNew() {
        return passwordNew;
    }
}
