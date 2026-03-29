package com.ug.ug_inventory_management.common.dtos.Employee;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "DTO to send the response from employee service handler")
public class ResponseEmployeeDTO {
    @Schema(description = "Employee table ID", example = "1234")
    private Long id;

    @Schema(description = "Employee ID", example = "1234")
    private Long eId;

    @Schema(description = "Employee working unit name", example = "New York Unit")
    private String unitName;

    @Schema(description = "Employee allocation date")
    private LocalDate allocation;

    public ResponseEmployeeDTO() {
    }

    public ResponseEmployeeDTO(Long id, Long eId, String unitName, LocalDate allocation) {
        this.id = id;
        this.eId = eId;
        this.unitName = unitName;
        this.allocation = allocation;
    }


    // Getters
    public Long getId() { return id; }

    public Long geteId() { return eId; }

    public String getUnitName() { return unitName; }

    public LocalDate getAllocation() { return allocation; }

    // toString
    @Override
    public String toString() {
        return "ResponseEmployeeDTO{" +
                "id=" + id +
                ", eId=" + eId +
                ", unitName='" + unitName + '\'' +
                ", allocation=" + allocation +
                '}';
    }
}
