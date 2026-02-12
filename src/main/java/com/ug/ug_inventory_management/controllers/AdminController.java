package com.ug.ug_inventory_management.controllers;

import com.ug.ug_inventory_management.common.dtos.AdminDTO;
import com.ug.ug_inventory_management.models.Admin;
import com.ug.ug_inventory_management.services.AdminServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hkfu")
public class AdminController {

    private final AdminServices adminServices;

    public AdminController(AdminServices adminServices) {
        this.adminServices = adminServices;
    }

    // Create admin controller
    @PostMapping("/")
    public ResponseEntity<AdminDTO> createAdmin(@RequestBody Admin admin) {
        AdminDTO admin_res = adminServices.createAdmin(admin);
        System.out.println(admin_res);

        return ResponseEntity.ok(admin_res);
    }
}
