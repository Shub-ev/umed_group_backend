package com.ug.ug_inventory_management.common.dtos;

public class AdminNameUpdateDTO {
    private Long id;
    private String oldName;
    private String newName;
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
