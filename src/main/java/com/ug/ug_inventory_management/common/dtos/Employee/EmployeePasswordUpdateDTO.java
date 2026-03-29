package com.ug.ug_inventory_management.common.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for updating employee password")
public class EmployeePasswordUpdateDTO {

    @Schema(description = "Employee ID")
    private Long eid;

    @Schema(description = "Employee working unit name")
    private String unit_name;

    @Schema(hidden = true)
    private String passwordPre;
    @Schema(hidden = true)
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
