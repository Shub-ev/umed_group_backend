package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.models.Admin;
import com.ug.ug_inventory_management.repositories.AdminRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

// @Service marks this class as SprintBean.
@Service
public class AdminServices {

    // Dependency injection by Constructor Injection
    private final AdminRepository adminRepository;
    public AdminServices(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public Admin createAdmin(@NonNull Admin admin){
        if((admin.getName() == null) || (admin.getName().trim().isEmpty())){
            return null;
        }
        admin = adminRepository.save(admin);
        // -> Replace this response with DTO (remove password from object)
        // -> Replace this response with DTO (remove password from object)
        return admin;
    }
}