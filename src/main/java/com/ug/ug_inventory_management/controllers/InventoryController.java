package com.ug.ug_inventory_management.controllers;
import org.springframework.security.core.Authentication;
import com.ug.ug_inventory_management.services.InventoryService;
import com.ug.ug_inventory_management.common.dtos.CreateInventoryRecordDTO;
import com.ug.ug_inventory_management.common.dtos.InventoryUpdateRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
//import com.ug.ug_inventory_management.common.security.CustomUserDetails;
import com.ug.ug_inventory_management.common.dtos.Employee.EmployeeResponseDTO;

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
    @GetMapping("/data/{templateId}/{unitName}")
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

//    @PostMapping("/update")
//    public ResponseEntity<?> updateInventory(
//            @RequestBody InventoryUpdateRequest req,
//            Authentication auth
//    ) {
//
//        Long eId;
//        String unitName;
//        String role = "EMPLOYEE";
//
//        if (auth != null && auth.getPrincipal() instanceof ResponseEmployeeDTO user) {
//            eId = user.geteId();
//            unitName = user.getUnitName();
//        } else {
//            eId = 1L;
//            unitName = "UNIT_1";
//        }
//
//        return inventoryService.updateInventory(req, role, eId, unitName);
//    }


    //--------------Authenticated User only make changes in the qty ----------//
    @PostMapping("/update")
    public ResponseEntity<?> updateInventory(
            @RequestBody InventoryUpdateRequest req,
            Authentication auth
    ) {
        Long eId = null;
        String unitName = null;
        String role = "EMPLOYEE";

        if (auth != null && auth.getPrincipal() instanceof EmployeeResponseDTO user) {
            eId = user.geteId();
            unitName = user.getUnitName();
        } else {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        return inventoryService.updateInventory(req, role, eId, unitName);
    }



    @GetMapping("/logs/employee")
    public ResponseEntity<?> getEmployeeLogs(Authentication auth) {
        String unitName;

        // since no auth implemented yet
        unitName = "UNIT_1";

        return ResponseEntity.ok(inventoryService.getLogsForEmployee(unitName));
    }

    @GetMapping("/logs/admin")
    public ResponseEntity<?> getAdminLogs(
            @RequestParam String unitName,
            @RequestParam(required = false) Long templateId
    ) {
        return ResponseEntity.ok(inventoryService.getLogsForAdmin(unitName, templateId));
    }

    @GetMapping("/logs/units")
    public ResponseEntity<?> getUnits() {
        return ResponseEntity.ok(inventoryService.getAllUnits());
    }



    // Employee logs
//        @GetMapping("/employee")
//        public ResponseEntity<?> getEmployeeLogs(Authentication auth) {
//            if(auth == null || !(auth.getPrincipal() instanceof ResponseEmployeeDTO user)) {
//                return ResponseEntity.status(401).body("User not authenticated");
//            }
//            String unitName = user.getUnitName();
//            return ResponseEntity.ok(inventoryService.getLogsForEmployee(unitName));
//        }
//    @GetMapping("/employee")
//    public ResponseEntity<?> getEmployeeLogs(Authentication auth) {
//        String unitName;
//        if(auth != null && auth.getPrincipal() instanceof ResponseEmployeeDTO user) {
//            unitName = user.getUnitName();
//        } else {
//            unitName = "UNIT_1"; // temporary for Postman testing
//        }
//        return ResponseEntity.ok(inventoryService.getLogsForEmployee(unitName));
//    }
//
//        // Admin logs (unitName + optional templateId)
//        @GetMapping("/admin")
//        public ResponseEntity<?> getAdminLogs(
//                @RequestParam String unitName,
//                @RequestParam(required = false) Long templateId
//        ) {
//            return ResponseEntity.ok(inventoryService.getLogsForAdmin(unitName, templateId));
//        }
//
//
//        @GetMapping("/units")
//        public ResponseEntity<?> getUnits() {
//            return ResponseEntity.ok(inventoryService.getAllUnits());
//        }


}