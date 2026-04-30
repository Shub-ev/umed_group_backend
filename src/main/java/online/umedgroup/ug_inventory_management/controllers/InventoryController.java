package online.umedgroup.ug_inventory_management.controllers;

import online.umedgroup.ug_inventory_management.common.dtos.InventorySearchResponseDTO;
import online.umedgroup.ug_inventory_management.common.dtos.Record.CreateInventoryRecordDTO;
import online.umedgroup.ug_inventory_management.common.dtos.Record.UpdateInventoryRecordDTO;
import online.umedgroup.ug_inventory_management.enums.ActionType;
import online.umedgroup.ug_inventory_management.security.CustomEmployeeDetails;
import online.umedgroup.ug_inventory_management.services.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final Logger log = LoggerFactory.getLogger(InventoryController.class);
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public String addInventory(@RequestBody CreateInventoryRecordDTO request) {
        log.info("Create new inventory: {}", request);
        inventoryService.addInventory(request);
        return "Inventory Saved";
    }

    @DeleteMapping("/{recordId}/{eId}")
    public ResponseEntity<?> deleteInventory(
            @PathVariable Long recordId,
            @PathVariable Long eId,
            @RequestParam String unitName   // or eId (better)
    ) {
        return inventoryService.deleteInventory(recordId, eId, unitName);
    }

    @GetMapping("/summary/{templateId}")
    public List<Map<String, String>> getSummary(@PathVariable Long templateId) {
        return inventoryService.getInventorySummary(templateId);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateInventory(@RequestBody UpdateInventoryRecordDTO req) {
        log.info("Update inventory record with: {}", req);
        return inventoryService.updateInventory(req);
    }

    @GetMapping("/logs/admin")
    public ResponseEntity<?> getAdminLogs(
            @RequestParam(required = false) String unitName,
            @RequestParam(required = false) String mainFieldValue,
            @RequestParam(required = false) ActionType action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        log.info("Admin logs fetch → unit: {}, mainFieldValue: {}, action: {}",
                unitName, mainFieldValue, action);

        return ResponseEntity.ok(
                inventoryService.getLogsForAdminFiltered(unitName, mainFieldValue, action, page, size)
        );
    }

    @GetMapping("/logs/employee")
    public ResponseEntity<?> getEmployeeLogs(
            Authentication authentication,
            @RequestParam(required = false) String mainFieldValue,
            @RequestParam(required = false) ActionType action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        CustomEmployeeDetails employeeDetails =
                (CustomEmployeeDetails) authentication.getPrincipal();

        Long eId = employeeDetails.getEmployee().getEId();

        log.info("Employee logs fetch → eId: {}, mainFieldValue: {}, action: {}",
                eId, mainFieldValue, action);

        return ResponseEntity.ok(
                inventoryService.getEmployeeLogsFiltered(eId, mainFieldValue, action, page, size)
        );
    }

    @GetMapping("/records/{templateId}")
    public ResponseEntity<InventorySearchResponseDTO> searchRecord(
            @PathVariable Long templateId,
            @RequestParam String field
    ) {
        log.info("Search records in template {} with main field value: {}", templateId, field);
        InventorySearchResponseDTO result = inventoryService.searchFromInventory(templateId, field);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/logs/units")
    public ResponseEntity<?> getUnits() {
        return ResponseEntity.ok(inventoryService.getAllUnits());
    }
}

