package com.ug.ug_inventory_management.common.dtos.Employee;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "DTO for responding employee data as response")
public class EmployeeDTO {

    @Schema(description = "Employee ID", example = "1234")
    private Long eId;

    @Schema(description = "Employee working unit name")
    private String unitName;

    public EmployeeDTO() {
    }

    public EmployeeDTO(Long eId, String unitName) {
        this.eId = eId;
        this.unitName = unitName;
    }


    // Getters
    public Long getEId() {
        return eId;
    }

    public String getUnitName() {
        return unitName;
    }


    // toString
    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "eId=" + eId +
                ", unitName='" + unitName + '\'' +
                '}';
    }
}
