package online.umedgroup.ug_inventory_management.common.dtos.Employee;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for login employee")
public class LoginEmployeeDTO {

    @Schema(description = "Employee ID", example = "1234")
    private Long eId;

    @Schema(hidden = true)
    private String password;

    public LoginEmployeeDTO() {
    }

    public LoginEmployeeDTO(Long eId, String password) {
        this.eId = eId;
        this.password = password;
    }


    // Getters
    public Long getEId() {
        return eId;
    }

    public String getPassword() { return password; }


    // toString
    @Override
    public String toString() {
        return "LoginEmployeeDTO{" +
                "eId=" + eId +
                ", password='" + password + '\'' +
                '}';
    }
}
