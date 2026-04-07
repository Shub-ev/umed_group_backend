package com.ug.ug_inventory_management.controllers;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import com.ug.ug_inventory_management.services.InventoryService;
import com.ug.ug_inventory_management.common.dtos.CreateInventoryRecordDTO;
import com.ug.ug_inventory_management.common.dtos.InventoryUpdateRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
//import com.ug.ug_inventory_management.common.security.CustomUserDetails;
import com.ug.ug_inventory_management.common.dtos.Employee.ResponseEmployeeDTO;

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

//--------------Testing Controller -------///

    @PostMapping("/update")
    public ResponseEntity<?> updateInventory(
            @RequestBody InventoryUpdateRequest req,
            Authentication auth
    ) {

        Long eId;
        String unitName;
        String role = "EMPLOYEE";

        if (auth != null && auth.getPrincipal() instanceof ResponseEmployeeDTO user) {
            eId = user.geteId();
            unitName = user.getUnitName();
        } else {
            eId = 1L;
            unitName = "UNIT_1";
        }

        return inventoryService.updateInventory(req, role, eId, unitName);
    }


    //--------------Authenticated User only make changes in the qty ----------//
//    @PostMapping("/update")
//    public ResponseEntity<?> updateInventory(
//            @RequestBody InventoryUpdateRequest req,
//            Authentication auth
//    ) {
//        Long eId = null;
//        String unitName = null;
//        String role = "EMPLOYEE";
//
//        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails user) {
//            eId = user.getEId();
//            unitName = user.getUnitName();
//            role = user.getAuthorities().iterator().next().getAuthority();
//        } else {
//            return ResponseEntity.status(401).body("User not authenticated");
//        }
//
//        return inventoryService.updateInventory(req, role, eId, unitName);
//    }
}