package com.ug.ug_inventory_management.controllers;
import org.springframework.security.core.Authentication;
import com.ug.ug_inventory_management.services.InventoryService;
import com.ug.ug_inventory_management.common.dtos.CreateInventoryRecordDTO;
import com.ug.ug_inventory_management.common.dtos.InventoryUpdateRecordDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
//import com.ug.ug_inventory_management.common.security.CustomUserDetails;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // ✅ CREATE INVENTORY
    @PostMapping
    public String addInventory(@RequestBody CreateInventoryRecordDTO request) {
        inventoryService.addInventory(request);
        return "Inventory Saved";
    }


    // #### Why {summary} name???
    @GetMapping("/summary/{templateId}")
    public List<Map<String, String>> getSummary(@PathVariable Long templateId) {
        return inventoryService.getInventorySummary(templateId);
    }

//  --------------Authenticated User only make changes in the qty ----------  //
    @PostMapping("/update")
    public ResponseEntity<?> updateInventory(@RequestBody InventoryUpdateRecordDTO req) {
        return inventoryService.updateInventory(req);
    }


    @GetMapping("/logs/employee/{eId}")
    public ResponseEntity<?> getEmployeeLogs(@PathVariable Long eId) {
        return ResponseEntity.ok(inventoryService.getEmployeeLogs(eId));
    }

    @GetMapping("/logs/admin")
    public ResponseEntity<?> getAdminLogs(
            @RequestParam(required = false) String unitName,
            @RequestParam(required = false) Long templateId
    ) {
        return ResponseEntity.ok(inventoryService.getLogsForAdmin(unitName, templateId));
    }

    @GetMapping("/logs/units")
    public ResponseEntity<?> getUnits() {
        return ResponseEntity.ok(inventoryService.getAllUnits());
    }


}