package com.ug.ug_inventory_management.common.dtos.Employee;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for creating employee")
public class CreateEmployeeDTO {

    @Schema(description = "Employee ID", example = "1234")
    private Long eId;

    @Schema(description = "Employee working unit name", example = "New York Unit")
    private String unitName;

    @Schema(hidden = true)
    private String password;

    public CreateEmployeeDTO() {
    }

    public CreateEmployeeDTO(Long eId, String unitName, String password) {
        this.eId = eId;
        this.unitName = unitName;
        this.password = password;
    }


    // Getters
    public Long getEId() {
        return eId;
    }

    public String getUnitName() {
        return unitName;
    }

    public String getPassword() { return password; }


    // toString
    @Override
    public String toString() {
        return "CreateEmployeeDTO{" +
                "eId=" + eId +
                ", unitName='" + unitName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
