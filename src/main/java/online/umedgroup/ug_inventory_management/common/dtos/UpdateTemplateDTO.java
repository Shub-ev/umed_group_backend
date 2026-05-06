package online.umedgroup.ug_inventory_management.common.dtos;

import java.util.List;

public class UpdateTemplateDTO {
    private boolean isRestricted;
    private List<Long> employeeIds;

    // Default constructor
    public UpdateTemplateDTO() {}

    // Constructor
    public UpdateTemplateDTO(boolean isRestricted, List<Long> employeeIds) {
        this.isRestricted = isRestricted;
        this.employeeIds = employeeIds;
    }

    // Getters and Setters
    public boolean isRestricted() {
        return isRestricted;
    }

    public void setRestricted(boolean isRestricted) {
        this.isRestricted = isRestricted;
    }

    public List<Long> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(List<Long> employeeIds) {
        this.employeeIds = employeeIds;
    }

    // toString()
    @Override
    public String toString() {
        return "UpdateTemplateDTO{" +
                "isRestricted=" + isRestricted +
                ", employeeIds=" + employeeIds +
                '}';
    }
}
