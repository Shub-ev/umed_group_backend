

package com.ug.ug_inventory_management.controllers;
import java.util.*;
import com.ug.ug_inventory_management.services.InventoryService;
import com.ug.ug_inventory_management.common.dtos.InventoryRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    // ✅ CREATE INVENTORY
    @PostMapping
    public String addInventory(@RequestBody InventoryRequest request) {
        service.saveRecord(request);
        return "Inventory Saved";
    }




    @GetMapping("/{templateId}/{unitId}")
    public List<Map<String, String>> getInventory(@PathVariable Long templateId,
                                                  @PathVariable Long unitId) {
        return service.getInventory(templateId, unitId);
    }

    @GetMapping("/summary/{templateId}")
    public List<Map<String, String>> getSummary(@PathVariable Long templateId) {
        return service.getInventorySummary(templateId);
    }
}