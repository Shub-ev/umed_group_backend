package online.umedgroup.ug_inventory_management.common.dtos.Employee;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for updating employee unit name")
public class EmployeeUnitNameUpdateDTO {

    @Schema(description = "Employee ID", example = "1234")
    private Long eId;

    @Schema(description = "Old working unit of employee", example = "New York unit")
    private String oldUnitName;

    @Schema(description = "New working unit of employee", example = "Pune unit")
    private String newUnitName;

    @Schema(hidden = true)
    private String password;

    public EmployeeUnitNameUpdateDTO() {
    }

    public EmployeeUnitNameUpdateDTO(Long eId, String oldUnitName, String newUnitName, String password) {
        this.eId = eId;
        this.oldUnitName = oldUnitName;
        this.newUnitName = newUnitName;
        this.password = password;
    }


    // getters
    public Long geteId() { return eId; }

    public String getOldUnitName() { return oldUnitName; }

    public String getNewUnitName() { return newUnitName; }

    public String getPassword() { return password; }


    // toString
    @Override
    public String toString() {
        return "EmployeeUnitNameUpdateDTO{" +
                "eId=" + eId +
                ", oldUnitName='" + oldUnitName + '\'' +
                ", newUnitName='" + newUnitName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
