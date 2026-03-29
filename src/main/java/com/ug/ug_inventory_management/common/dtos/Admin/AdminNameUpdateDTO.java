package com.ug.ug_inventory_management.common.dtos.Admin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for updating admin name.")
public class AdminNameUpdateDTO {

    @Schema(description = "Admin id", example = "1")
    private Long id;

    @Schema(description = "Admin old name", example = "John Deo")
    private String oldName;

    @Schema(description = "Admin new name", example = "Deo John")
    private String newName;

    @Schema(hidden = true)
    private String password;

    public AdminNameUpdateDTO(Long id, String oldName, String newName, String password) {
        this.id = id;
        this.oldName = oldName;
        this.newName = newName;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getOldName() {
        return oldName;
    }

    public String getNewName() {
        return newName;
    }

    public String getPassword() {
        return password;
    }
}
