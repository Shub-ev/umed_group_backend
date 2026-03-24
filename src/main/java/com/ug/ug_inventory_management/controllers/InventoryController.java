package com.ug.ug_inventory_management.controllers;

import com.ug.ug_inventory_management.services.InventoryService;
import com.ug.ug_inventory_management.common.dtos.CreateInventoryRecordDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    // ✅ CREATE INVENTORY
    @PostMapping
    public String addInventory(@RequestBody CreateInventoryRecordDTO request) {
        service.saveRecord(request);
        return "Inventory Saved";
    }

    // ✅ GET INVENTORY (IMPORTANT)
    @GetMapping("/{templateId}/{unitId}")
    public List<Map<String, String>> getInventory(@PathVariable Long templateId,
                                                  @PathVariable String unitName) {
        return service.getInventory(templateId, unitName);
    }

    @GetMapping("/summary/{templateId}")
    public List<Map<String, String>> getSummary(@PathVariable Long templateId) {
        return service.getInventorySummary(templateId);
    }
}