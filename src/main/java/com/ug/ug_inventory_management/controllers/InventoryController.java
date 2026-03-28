package com.ug.ug_inventory_management.controllers;

import com.ug.ug_inventory_management.services.InventoryService;
import com.ug.ug_inventory_management.common.dtos.CreateInventoryRecordDTO;
import com.ug.ug_inventory_management.common.dtos.InventoryUpdateRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

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
        inventoryService.saveRecord(request);
        return "Inventory Saved";
    }

    // ✅ GET INVENTORY (IMPORTANT)
    @GetMapping("/{templateId}/{unitName}")
    public List<Map<String, String>> getInventory(@PathVariable Long templateId,
                                                  @PathVariable String unitName) {
        return inventoryService.getInventory(templateId, unitName);
    }

    // #### Why {summary} name???
    @GetMapping("/summary/{templateId}")
    public List<Map<String, String>> getSummary(@PathVariable Long templateId) {
        return inventoryService.getInventorySummary(templateId);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateInventory(
            @RequestBody InventoryUpdateRequest request,
            @RequestHeader("role") String role) {
        return inventoryService.updateInventory(request, role);
    }
}