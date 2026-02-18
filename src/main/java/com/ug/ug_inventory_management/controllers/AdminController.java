package com.ug.ug_inventory_management.controllers;

import com.ug.ug_inventory_management.common.dtos.AdminDTO;
import com.ug.ug_inventory_management.common.dtos.AdminPasswordUpdateDTO;
import com.ug.ug_inventory_management.models.Admin;
import com.ug.ug_inventory_management.services.AdminServices;
import org.springframework.http.HttpStatus;
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

        return ResponseEntity.status(HttpStatus.CREATED).body(admin_res);
    }

    @PostMapping("/login")
    public ResponseEntity<AdminDTO> loginAdmin(@RequestBody Admin admin) {
        AdminDTO admin_res = adminServices.loginAdmin(admin);
        System.out.println(admin_res);

        return ResponseEntity.ok(admin_res);
    }

    @PatchMapping("/update/name")
    public ResponseEntity<AdminDTO> updateAdminName(@RequestBody Admin admin) {
        AdminDTO adminDTO = adminServices.updateAdminName(admin);
        System.out.println(adminDTO);

        return ResponseEntity.ok(adminDTO);
    }

    @PatchMapping("/update/password")
    public ResponseEntity<AdminDTO> updateAdminPassword(@RequestBody AdminPasswordUpdateDTO adminpass) {
        AdminDTO adminDTO = adminServices.updateAdminPassword(adminpass);
        System.out.println(adminDTO);

        return ResponseEntity.ok(adminDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<AdminDTO> deleteAdmin(@RequestBody Admin admin) {
        AdminDTO adminDTO = adminServices.deleteAdmin(admin);
        System.out.println(adminDTO);

        return ResponseEntity.ok(adminDTO);
    }
}
