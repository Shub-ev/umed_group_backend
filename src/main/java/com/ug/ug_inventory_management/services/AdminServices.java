package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.common.dtos.AdminDTO;
import com.ug.ug_inventory_management.common.dtos.AdminPasswordUpdateDTO;
import com.ug.ug_inventory_management.common.exceptions.AdminNotFoundException;
import com.ug.ug_inventory_management.models.Admin;
import com.ug.ug_inventory_management.repositories.AdminRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// @Service marks this class as SprintBean.
@Service
public class AdminServices {

    // Dependency injection by Constructor Injection
    private final AdminRepository adminRepository;
    public AdminServices(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public AdminDTO createAdmin(@NonNull Admin admin) {
        // Check if admin input have required fields
        if((admin.getName() == null) || (admin.getName().trim().isEmpty())) {
            // return appropriate server response
            return null;
        }
        Admin repoResponse = adminRepository.save(admin);

        // Create AdminDTO and return
        AdminDTO adminDto = new AdminDTO(repoResponse.getId(), repoResponse.getName());
        return adminDto;
    }

    public AdminDTO loginAdmin(@NonNull Admin admin) {
        // Check if admin input have required fields
        if((admin.getName() == null) || (admin.getName().trim().isEmpty())) {
            // return appropriate server response
            return null;
        } else if((admin.getPassword() == null) || (admin.getPassword().trim().isEmpty())) {
            // return appropriate server response
            return null;
        }

        // Get admin data and send to client
        Admin foundAdmin = adminRepository.findByName(admin.getName())
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with name: " + admin.getName()));

        // check the password
        if(admin.getPassword().equals(foundAdmin.getPassword())) {
            AdminDTO adminDto = new AdminDTO(foundAdmin.getId(), foundAdmin.getName());
            return adminDto;
        } else {
            return null;
        }
    }

    public AdminDTO updateAdminName(@NonNull Admin admin) {
        // Check if admin input have required fields
        if((admin.getName() == null) || (admin.getName().trim().isEmpty())) {
            // return appropriate server response
            return null;
        } else if((admin.getPassword() == null) || (admin.getPassword().trim().isEmpty())) {
            // return appropriate server response
            return null;
        }

        Admin foundAdmin = adminRepository.findById(admin.getId())
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with name: " + admin.getName()));

        // verify password before updating the admin entity
        if(foundAdmin.getPassword().equals(admin.getPassword())) {
            foundAdmin.setName(admin.getName());
            Admin saveRes = adminRepository.save(foundAdmin);
            if (saveRes != null) {
                AdminDTO adminDTO = new AdminDTO(saveRes.getId(), saveRes.getName());
                return adminDTO;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public AdminDTO updateAdminPassword(@NonNull AdminPasswordUpdateDTO adminpass) {
        // Check if admin input have required fields
        if((adminpass.getName() == null) || (adminpass.getName().trim().isEmpty())) {
            // return appropriate server response
            return null;
        } else if((adminpass.getPasswordPre() == null) || (adminpass.getPasswordPre().trim().isEmpty())) {
            // return appropriate server response
            return null;
        } else if((adminpass.getPasswordNew() == null) || (adminpass.getPasswordNew().trim().isEmpty())) {
            // return appropriate server response
            return null;
        }

        // get the admin entity
        Admin foundAdmin = adminRepository.findByName(adminpass.getName())
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with name: " + adminpass.getName()));

        // verify password
        if(foundAdmin.getPassword().equals(adminpass.getPasswordPre())) {
            foundAdmin.setPassword(adminpass.getPasswordNew());
            Admin saveRes = adminRepository.save(foundAdmin);
            if(saveRes != null) {
                // **** No need to send back the admin DTO as we didnt change anything meaningfull
                // **** for client side
                AdminDTO adminDTO = new AdminDTO(saveRes.getId(), saveRes.getName());
                return adminDTO;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Transactional
    public AdminDTO deleteAdmin(@NonNull Admin admin) {
        if(admin.getName() == null || admin.getName().trim().isEmpty()) {
            return null;
        } else if(admin.getPassword() == null || admin.getPassword().trim().isEmpty()) {
            return null;
        }

        Admin foundAdmin = adminRepository.findByName(admin.getName())
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with name: " + admin.getName()));

        if(foundAdmin.getPassword().equals(admin.getPassword())) {
            Optional<Admin> repoResponse2 = adminRepository.deleteByName(admin.getName());
            if(repoResponse2.isPresent()) {
                return new AdminDTO(admin.getId(), admin.getName());
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}