package com.ug.ug_inventory_management.controllers;

import com.ug.ug_inventory_management.common.dtos.CreateInventoryRecordDTO;
import com.ug.ug_inventory_management.common.dtos.InventoryUpdateRecordDTO;
import com.ug.ug_inventory_management.enums.ActionType;
import com.ug.ug_inventory_management.security.CustomEmployeeDetails;
import com.ug.ug_inventory_management.services.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
        inventoryService.addInventory(request);
        return "Inventory Saved";
    }

    @GetMapping("/summary/{templateId}")
    public List<Map<String, String>> getSummary(@PathVariable Long templateId) {
        return inventoryService.getInventorySummary(templateId);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateInventory(@RequestBody InventoryUpdateRecordDTO req) {
        return inventoryService.updateInventory(req);
    }

    @GetMapping("/logs/admin")
    public ResponseEntity<?> getAdminLogs(
            @RequestParam(required = false) String unitName,
            @RequestParam(required = false) String templateName,
            @RequestParam(required = false) ActionType action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        log.info("Admin logs fetch → unit: {}, template: {}, action: {}",
                unitName, templateName, action);

        return ResponseEntity.ok(
                inventoryService.getLogsForAdminFiltered(unitName, templateName, action, page, size)
        );
    }

    @GetMapping("/logs/employee")
    public ResponseEntity<?> getEmployeeLogs(
            Authentication authentication,
            @RequestParam(required = false) String templateName,
            @RequestParam(required = false) ActionType action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        CustomEmployeeDetails employeeDetails =
                (CustomEmployeeDetails) authentication.getPrincipal();

        Long eId = employeeDetails.getEmployee().getEId();

        log.info("Employee logs fetch → eId: {}, template: {}, action: {}",
                eId, templateName, action);

        return ResponseEntity.ok(
                inventoryService.getEmployeeLogsFiltered(eId, templateName, action, page, size)
        );
    }
    @GetMapping("/logs/units")
    public ResponseEntity<?> getUnits() {
        return ResponseEntity.ok(inventoryService.getAllUnits());
    }
}

