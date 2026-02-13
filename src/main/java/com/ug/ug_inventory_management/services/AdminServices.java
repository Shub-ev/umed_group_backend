package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.common.dtos.AdminDTO;
import com.ug.ug_inventory_management.common.dtos.AdminPasswordUpdateDTO;
import com.ug.ug_inventory_management.models.Admin;
import com.ug.ug_inventory_management.repositories.AdminRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

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
        Optional<Admin> repoResponse = adminRepository.findByName(admin.getName());
        if(repoResponse.isPresent()) {
            Admin foundAdmin = repoResponse.get();

            // check the password
            if(admin.getPassword().equals(foundAdmin.getPassword())) {
                AdminDTO adminDto = new AdminDTO(foundAdmin.getId(), foundAdmin.getName());
                return adminDto;
            } else {
                return null;
            }
        } else {
            // return appropriate server response
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

        Optional<Admin> repoResponse = adminRepository.findById(admin.getId());
        if(repoResponse.isPresent()) {
            Admin foundAdmin = repoResponse.get();

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
        Optional<Admin> repoResponse = adminRepository.findByName(adminpass.getName());
        if(repoResponse.isPresent()) {
            Admin foundAdmin = repoResponse.get();

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
        } else {
            return null;
        }
    }
}