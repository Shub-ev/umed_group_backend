//package com.ug.ug_inventory_management.controllers;
//import com.ug.ug_inventory_management.services.InventoryService;
//import com.ug.ug_inventory_management.common.dtos.InventoryRequest;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/inventory")
//public class InventoryController {
//
//    private final InventoryService service;
//
//    public InventoryController(InventoryService service) {
//        this.service = service;
//    }
//
//    @PostMapping("/")
//    public String addInventory(@RequestBody InventoryRequest request) {
//        service.saveRecord(request);
//        return "Inventory Saved";
//    }
//}

package com.ug.ug_inventory_management.controllers;

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

    // ✅ GET INVENTORY (IMPORTANT)
    @GetMapping("/{templateId}/{unitId}")
    public Object getInventory(
            @PathVariable Long templateId,
            @PathVariable Long unitId) {

        return service.getInventory(templateId, unitId);
    }
}