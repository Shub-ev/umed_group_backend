package com.ug.ug_inventory_management.controllers;

import com.ug.ug_inventory_management.common.dtos.Admin.AdminDTO;
import com.ug.ug_inventory_management.common.dtos.Admin.AdminResponseDTO;
import com.ug.ug_inventory_management.common.dtos.Admin.AdminNameUpdateDTO;
import com.ug.ug_inventory_management.common.dtos.Admin.AdminPasswordUpdateDTO;
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
        log.info("Updating admin name for admin: {}", admin.getOldName());
        AdminResponseDTO adminResponseDTO = adminServices.updateAdminName(admin);
        log.info("Admin name updated successfully from {} to {}", admin.getOldName(), admin.getNewName());

        return ResponseEntity.ok(adminResponseDTO);
    }

    @PatchMapping("/update/password")
    public ResponseEntity<AdminResponseDTO> updateAdminPassword(@RequestBody AdminPasswordUpdateDTO adminpass) {
        log.info("Updating admin password for admin: {}", adminpass.getName());
        AdminResponseDTO adminResponseDTO = adminServices.updateAdminPassword(adminpass);
        log.info("Password updated successfully for admin: {}", adminpass.getId());

        return ResponseEntity.ok(adminResponseDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<AdminResponseDTO> deleteAdmin(@RequestBody AdminDTO adminDTO) {
        log.info("Deleting admin with name: {}", adminDTO.getName());
        AdminResponseDTO adminResponseDTO = adminServices.deleteAdmin(adminDTO);
        log.info("Admin deleted successfully with name: {}", adminDTO.getName());

        return ResponseEntity.ok(adminResponseDTO);
    }

    @GetMapping("/count")
    public Long getAdminCount() {
        log.info("Fetching admin count");
        Long adminCount = adminServices.getAdminCount();
        log.info("Admin count fetched: {}", adminCount);
        return adminCount;
    }

    @GetMapping("/get_admins")
    public ResponseEntity<List<AdminResponseDTO>> getAdmins() {
        log.info("Fetching admins");
        List<AdminResponseDTO> admins = adminServices.getAdmins();
        log.info("Admins fetched");
        return ResponseEntity.ok(admins);
    }
}
