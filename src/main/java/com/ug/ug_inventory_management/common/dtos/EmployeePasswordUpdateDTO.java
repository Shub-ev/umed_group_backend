package com.ug.ug_inventory_management.common.dtos;

public class EmployeePasswordUpdateDTO {
    private Long eid;
    private String unit_name;
    private String passwordPre;
    private String passwordNew;

    public EmployeePasswordUpdateDTO(Long eid, String unit_name, String passwordPre, String passwordNew) {
        this.eid = eid;
        this.unit_name = unit_name;
        this.passwordPre = passwordPre;
        this.passwordNew = passwordNew;
    }

    public Long getEid() {
        return eid;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public String getPasswordPre() {
        return passwordPre;
    }

    public String getPasswordNew() {
        return passwordNew;
    }
}
