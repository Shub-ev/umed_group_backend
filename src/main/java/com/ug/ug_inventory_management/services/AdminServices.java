package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.models.Admin;
import com.ug.ug_inventory_management.repositories.AdminRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class AdminServices {
    @Autowired
    AdminRepository adminRepository;

    public Admin createAdmin( @NonNull Admin admin){
        if(admin.getName()==null || admin.getName().trim().isEmpty()){
            return null;
        }
        admin=adminRepository.save(admin);
        return admin;

    }
}
