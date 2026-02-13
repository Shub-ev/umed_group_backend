package com.ug.ug_inventory_management.common.dtos;

import java.time.LocalDate;

public class EmployeeDTO {
    private Long eid;
    private String unit_name;
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
