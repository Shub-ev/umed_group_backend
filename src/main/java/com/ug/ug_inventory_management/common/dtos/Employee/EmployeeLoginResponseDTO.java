package com.ug.ug_inventory_management.common.dtos.Employee;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "DTO to send the response from employee service handler")
public class EmployeeLoginResponseDTO {
    @Schema(description = "Employee table ID", example = "1234")
    private Long id;

    @Schema(description = "Employee ID", example = "1234")
    private Long eId;

    @Schema(description = "Employee working unit name", example = "New York Unit")
    private String unitName;

    @Schema(description = "Employee allocation date")
    private LocalDate allocation;

    @Schema(description = "JWT token")
    private String token;

    public EmployeeLoginResponseDTO() {
    }

    public EmployeeLoginResponseDTO(Long id, Long eId, String unitName, LocalDate allocation, String token) {
        this.id = id;
        this.eId = eId;
        this.unitName = unitName;
        this.allocation = allocation;
        this.token = token;
    }


    // Getters
    public Long getId() { return id; }

    public Long getEId() { return eId; }

    public String getUnitName() { return unitName; }

    public LocalDate getAllocation() { return allocation; }

    public String getToken() { return token; }


    public void setEId(Long eId) {
        this.eId = eId;
    }

    // toString
    @Override
    public String toString() {
        return "EmployeeLoginResponseDTO{" +
                "id=" + id +
                ", eId=" + eId +
                ", unitName='" + unitName + '\'' +
                ", allocation=" + allocation +
                ", token='" + token + '\'' +
                '}';
    }
}
