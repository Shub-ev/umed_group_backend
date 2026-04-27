package online.umedgroup.ug_inventory_management.common.dtos;

public class StockAlertDTO {

    private String unitName;
    private String mainFieldValue;
    private String templateName;
    private int stock;

    public StockAlertDTO() {
    }

    public StockAlertDTO(String unitName, String mainFieldValue,String templateName, int stock) {
        this.unitName = unitName;
        this.mainFieldValue=mainFieldValue;
        this.templateName = templateName;
        this.stock = stock;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getMainFieldValue() {
        return mainFieldValue;
    }

    public void setMainFieldValue(String mainFieldValue) {
        this.mainFieldValue = mainFieldValue;
    }
}