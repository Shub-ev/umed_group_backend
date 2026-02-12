package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.common.dtos.AdminDTO;
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

    public AdminDTO createAdmin(@NonNull Admin admin){
        // Check if admin input have required fields
        if((admin.getName() == null) || (admin.getName().trim().isEmpty())) {
            return null;
        }
        admin = adminRepository.save(admin);

        // Create AdminDTO and return
        AdminDTO adminDto = new AdminDTO(admin.getId(), admin.getName());
        return adminDto;
    }
}