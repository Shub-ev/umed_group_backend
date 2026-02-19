package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.common.dtos.AdminDTO;
import com.ug.ug_inventory_management.common.dtos.AdminNameUpdateDTO;
import com.ug.ug_inventory_management.common.dtos.AdminPasswordUpdateDTO;
import com.ug.ug_inventory_management.common.exceptions.AdminNotFoundException;
import com.ug.ug_inventory_management.common.exceptions.IllegalArgumentException;
import com.ug.ug_inventory_management.common.exceptions.WrongPasswordException;
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
            throw new IllegalArgumentException("Admin name must not be blank");
        }
        Admin repoResponse = adminRepository.save(admin);
        // Create AdminDTO and return
        AdminDTO adminDto = new AdminDTO(repoResponse.getId(), repoResponse.getName());
        return adminDto;
    }

    public AdminDTO loginAdmin(@NonNull Admin admin) {
        // Check if admin input have required fields
        if((admin.getName() == null) || (admin.getName().trim().isEmpty())) {
            throw new IllegalArgumentException("Admin name must not be blank");
        } else if((admin.getPassword() == null) || (admin.getPassword().trim().isEmpty())) {
            throw new IllegalArgumentException("Admin password must not be blank");
        }

        Admin foundAdmin = adminRepository.findByName(admin.getName())
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with name: " + admin.getName()));

        // check the password
        if(admin.getPassword().equals(foundAdmin.getPassword())) {
            AdminDTO adminDto = new AdminDTO(foundAdmin.getId(), foundAdmin.getName());
            return adminDto;
        } else {
            throw new WrongPasswordException("Password is incorrect for admin: " + admin.getName());
        }
    }

    public AdminDTO updateAdminName(@NonNull AdminNameUpdateDTO admin) {
        // Check if admin input have required fields
        if((admin.getOldName() == null) || (admin.getOldName().trim().isEmpty())) {
            throw new IllegalArgumentException("admin old name must not be blank");
        }
        if((admin.getNewName() == null) || (admin.getNewName().trim().isEmpty())) {
            throw new IllegalArgumentException("admin new name must not be blank");
        }
        if((admin.getPassword() == null) || (admin.getPassword().trim().isEmpty())) {
            throw new IllegalArgumentException("admin password must not be blank");
        }

        Admin foundAdmin = adminRepository.findByName(admin.getOldName())
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with name: " + admin.getOldName()));

        if(!foundAdmin.getPassword().equals(admin.getPassword())) {
            throw new WrongPasswordException("Password is incorrect for admin: " + admin.getOldName());
        }
        if(adminRepository.exitsByName(admin.getOldName())) {
            throw new java.lang.IllegalArgumentException("admin with this name already exists");
        }

        foundAdmin.setName(admin.getNewName());

        // verify password before updating the admin entity
        if(foundAdmin.getPassword().equals(admin.getPassword())) {

            Admin saveRes = adminRepository.save(foundAdmin);
            if (saveRes != null) {
                AdminDTO adminDTO = new AdminDTO(saveRes.getId(), saveRes.getName());
                return adminDTO;
            } else {
                return null;
            }
        } else {

        }
    }

    public AdminDTO updateAdminPassword(@NonNull AdminPasswordUpdateDTO adminpass) {
        // Check if admin input have required fields
        if((adminpass.getName() == null) || (adminpass.getName().trim().isEmpty())) {
            throw new IllegalArgumentException("admin name must not be blank");
        } else if((adminpass.getPasswordPre() == null) || (adminpass.getPasswordPre().trim().isEmpty())) {
            throw new IllegalArgumentException("Old password must not be blank");
        } else if((adminpass.getPasswordNew() == null) || (adminpass.getPasswordNew().trim().isEmpty())) {
            throw new IllegalArgumentException("New password must not be blank");
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