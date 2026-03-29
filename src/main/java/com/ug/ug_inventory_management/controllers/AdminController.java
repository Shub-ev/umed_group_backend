package com.ug.ug_inventory_management.controllers;

import com.ug.ug_inventory_management.common.dtos.AdminDTO;
import com.ug.ug_inventory_management.common.dtos.AdminResponseDTO;
import com.ug.ug_inventory_management.common.dtos.AdminNameUpdateDTO;
import com.ug.ug_inventory_management.common.dtos.AdminPasswordUpdateDTO;
import com.ug.ug_inventory_management.models.Admin;
import com.ug.ug_inventory_management.services.AdminServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/hkfu")
public class AdminController {

    private final AdminServices adminServices;
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    public AdminController(AdminServices adminServices) {
        this.adminServices = adminServices;
    }

    // Create admin controller
    @PostMapping("/")
    public ResponseEntity<AdminResponseDTO> createAdmin(@RequestBody AdminDTO admin) {
        log.info("Creating admin with name: {}", admin.getName());
        AdminResponseDTO admin_res = adminServices.createAdmin(admin);
        log.info("Created Admin: {}", admin_res);

        return ResponseEntity.status(HttpStatus.CREATED).body(admin_res);
    }

    @PostMapping("/login")
    public ResponseEntity<AdminResponseDTO> loginAdmin(@RequestBody AdminDTO admin) {
        log.info("Login with admin name: {}", admin.getName());
        AdminResponseDTO admin_res = adminServices.loginAdmin(admin);
        log.info("Login process completed, response: {}", admin_res);

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

    @GetMapping("/get_admins")
    public ResponseEntity<List<AdminResponseDTO>> getAdmins() {
        List<AdminResponseDTO> admins = adminServices.getAdmins();
        return ResponseEntity.ok(admins);
    }
}
