package com.ug.ug_inventory_management.common.dtos.Employee;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for updating employee password")
public class EmployeePasswordUpdateDTO {

    @Schema(description = "Employee ID")
    private Long eId;

    @Schema(description = "Employee working unit name")
    private String unitName;

    @Schema(hidden = true)
    private String passwordPre;

    @Schema(hidden = true)
    private String passwordNew;

    public EmployeePasswordUpdateDTO() {
    }

    public EmployeePasswordUpdateDTO(Long eId, String unitName, String passwordPre, String passwordNew) {
        this.eId = eId;
        this.unitName = unitName;
        this.passwordPre = passwordPre;
        this.passwordNew = passwordNew;
    }


    // getters
    public Long getEId() {
        return eId;
    }

    public String getUnitName() {
        return unitName;
    }

    public String getPasswordPre() {
        return passwordPre;
    }

    public String getPasswordNew() {
        return passwordNew;
    }


    // toString
    @Override
    public String toString() {
        return "EmployeePasswordUpdateDTO{" +
                "eid=" + eId +
                ", unit_name='" + unitName + '\'' +
                ", passwordPre='" + passwordPre + '\'' +
                ", passwordNew='" + passwordNew + '\'' +
                '}';
    }
}
