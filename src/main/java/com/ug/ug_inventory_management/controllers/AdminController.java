package com.ug.ug_inventory_management.controllers;

import com.ug.ug_inventory_management.common.dtos.AdminResponseDTO;
import com.ug.ug_inventory_management.common.dtos.AdminNameUpdateDTO;
import com.ug.ug_inventory_management.common.dtos.AdminPasswordUpdateDTO;
import com.ug.ug_inventory_management.models.Admin;
import com.ug.ug_inventory_management.services.AdminServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/hkfu")
public class AdminController {

    private final AdminServices adminServices;

    public AdminController(AdminServices adminServices) {
        this.adminServices = adminServices;
    }

    // Create admin controller
    @PostMapping("/")
    public ResponseEntity<AdminResponseDTO> createAdmin(@RequestBody Admin admin) {
        AdminResponseDTO admin_res = adminServices.createAdmin(admin);
        System.out.println(admin_res);

        return ResponseEntity.status(HttpStatus.CREATED).body(admin_res);
    }

    @PostMapping("/login")
    public ResponseEntity<AdminResponseDTO> loginAdmin(@RequestBody Admin admin) {
        AdminResponseDTO admin_res = adminServices.loginAdmin(admin);
        System.out.println(admin_res);

        return ResponseEntity.ok(admin_res);
    }

    @PatchMapping("/update/name")
    public ResponseEntity<AdminResponseDTO> updateAdminName(@RequestBody AdminNameUpdateDTO admin) {
        AdminResponseDTO adminResponseDTO = adminServices.updateAdminName(admin);
        System.out.println(adminResponseDTO);

        return ResponseEntity.ok(adminResponseDTO);
    }

    @PatchMapping("/update/password")
    public ResponseEntity<AdminResponseDTO> updateAdminPassword(@RequestBody AdminPasswordUpdateDTO adminpass) {
        AdminResponseDTO adminResponseDTO = adminServices.updateAdminPassword(adminpass);
        System.out.println(adminResponseDTO);

        return ResponseEntity.ok(adminResponseDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<AdminResponseDTO> deleteAdmin(@RequestBody Admin admin) {
        AdminResponseDTO adminResponseDTO = adminServices.deleteAdmin(admin);
        System.out.println(adminResponseDTO);

        return ResponseEntity.ok(adminResponseDTO);
    }

    @GetMapping("/count")
    public Long getAdminCount() {
        return adminServices.getAdminCount();
    }
}
