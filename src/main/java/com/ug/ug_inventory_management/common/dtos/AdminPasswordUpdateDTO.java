package com.ug.ug_inventory_management.common.dtos;

public class AdminPasswordUpdateDTO {
    private Long id;
    private String name;
    private String passwordPre;
    private String passwordNew;

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
