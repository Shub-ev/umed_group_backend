package online.umedgroup.ug_inventory_management.common.dtos;

public class UnitNameDTO {
    private String unitName;

    /* Constructors */
    public UnitNameDTO() {
    }
    public UnitNameDTO(String unitName) {
        this.unitName = unitName;
    }

    /* Getters */
    public String getUnitName() {
        return unitName;
    }


    /* Setters */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }


    /* Override toStirng() */
    @Override
    public String toString() {
        return "UnitNameDTO{" +
                "unitName='" + unitName + '\'' +
                '}';
    }
}
