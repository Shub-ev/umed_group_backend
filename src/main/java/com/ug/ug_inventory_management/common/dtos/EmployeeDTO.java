package com.ug.ug_inventory_management.common.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "DTO for responding employee data as response")
public class EmployeeDTO {

    @Schema(description = "Employee ID")
    private Long eId;

    @Schema(description = "Employee working unit name")
    private String unitName;

    @Schema(description = "Employee allocation data")
    private LocalDate allocation;

    public EmployeeDTO(Long eId, String unitName, LocalDate allocation) {
        this.eId = eId;
        this.unitName = unitName;
        this.allocation = allocation;
    }

    public Long getEId() {
        return eId;
    }

    public String getUnitName() {
        return unitName;
    }

    public LocalDate getAllocation() {
        return allocation;
    }

    @Override
    public String toString() {
        return "EId: " + this.eId + "\tUnit Name: " + this.unitName + "\tAllocation: "
                + this.allocation;
    }
}
