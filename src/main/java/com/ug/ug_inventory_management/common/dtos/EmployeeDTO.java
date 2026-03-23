package com.ug.ug_inventory_management.common.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "DTO for responding employee data as response")
public class EmployeeDTO {

    @Schema(description = "Employee ID")
    private Long eid;

    @Schema(description = "Employee working unit name")
    private String unit_name;

    @Schema(description = "Employee allocation data")
    private LocalDate allocation;

    public EmployeeDTO(Long eid, String unit_name, LocalDate allocation) {
        this.eid = eid;
        this.unit_name = unit_name;
        this.allocation = allocation;
    }

    public Long getEid() {
        return eid;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public LocalDate getAllocation() {
        return allocation;
    }

    @Override
    public String toString() {
        return "EID: " + this.eid + "\tUnit Name: " + this.unit_name + "\tAllocation: "
                + this.allocation;
    }
}
