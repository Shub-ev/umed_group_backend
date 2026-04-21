package com.ug.ug_inventory_management.common.dtos.Employee;

public class EmployeePasswordVerifyDTO {

    private Long eId;
    private String password;

    public EmployeePasswordVerifyDTO() {
    }

    public EmployeePasswordVerifyDTO(Long eId, String password) {
        this.eId = eId;
        this.password = password;
    }

    public Long getEId() {
        return eId;
    }

    public void setEId(Long eId) {
        this.eId = eId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}