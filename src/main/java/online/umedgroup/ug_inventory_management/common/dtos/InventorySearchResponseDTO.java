package online.umedgroup.ug_inventory_management.common.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "DTO returns records matching search text along with sum of stocks")
public class InventorySearchResponseDTO {

    @Schema(description = "List of matching records")
    private List<Map<String, String>> records;

    @Schema(description = "Sum of stocks of matching records")
    private Long stockSum;


    // constructors
    public InventorySearchResponseDTO() {
    }
    public InventorySearchResponseDTO(List<Map<String, String>> records, Long stockSum) {
        this.records = records;
        this.stockSum = stockSum;
    }


    // setters
    public void setRecords(List<Map<String, String>> records) {
        this.records = records;
    }

    public void setStockSum(Long stockSum) {
        this.stockSum = stockSum;
    }


    // getters
    public List<Map<String, String>> getRecords() {
        return records;
    }

    public Long getStockSum() {
        return stockSum;
    }


    // toString()
    @Override
    public String toString() {
        return "InventorySearchResponseDTO{" +
                "records=" + records +
                ", stockSum=" + stockSum +
                '}';
    }
}
