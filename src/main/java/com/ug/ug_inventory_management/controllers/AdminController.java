package com.ug.ug_inventory_management.controllers;

import com.ug.ug_inventory_management.models.Admin;
import com.ug.ug_inventory_management.services.AdminServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hfke")
public class AdminController {
    @Autowired
    AdminServices adminServices;

    @PostMapping("/")
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin){
        Admin savedAdmin = adminServices.createAdmin(admin);
        System.out.println("Admin Saved");
        return ResponseEntity.ok(savedAdmin);
    }

}
