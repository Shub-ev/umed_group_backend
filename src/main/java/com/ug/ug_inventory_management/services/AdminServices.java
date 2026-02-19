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
        if((admin.getName() == null) || (admin.getName().trim().isEmpty())) {
            throw new IllegalArgumentException("Admin name must not be blank");
        }
        if((admin.getPassword() == null) || (admin.getPassword().trim().isEmpty())) {
            throw new IllegalArgumentException("Admin password must not be blank");
        }

        // trim admin name and password before using
        admin.setName(admin.getName().trim());
        admin.setPassword(admin.getPassword().trim());

        if(adminRepository.existsByName(admin.getName())) {
            throw new IllegalArgumentException("Admin already exists with name: " + admin.getName());
        }
        Admin repoResponse = adminRepository.save(admin);
        return new AdminDTO(repoResponse.getId(), repoResponse.getName());
    }

    public AdminDTO loginAdmin(@NonNull Admin admin) {
        if((admin.getName() == null) || (admin.getName().trim().isEmpty())) {
            throw new IllegalArgumentException("Admin name must not be blank");
        }
        if((admin.getPassword() == null) || (admin.getPassword().trim().isEmpty())) {
            throw new IllegalArgumentException("Admin password must not be blank");
        }

        // trim admin name and password before using
        admin.setName(admin.getName().trim());
        admin.setPassword(admin.getPassword().trim());

        Admin foundAdmin = adminRepository.findByName(admin.getName())
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with name: " + admin.getName()));

        if(!admin.getPassword().equals(foundAdmin.getPassword())) {
            throw new WrongPasswordException("Invalid credentials");
        }
        return new AdminDTO(foundAdmin.getId(), foundAdmin.getName());
    }

    @Transactional
    public AdminDTO updateAdminName(@NonNull AdminNameUpdateDTO admin) {
        if((admin.getOldName() == null) || (admin.getOldName().trim().isEmpty())) {
            throw new IllegalArgumentException("admin old name must not be blank");
        }
        if((admin.getNewName() == null) || (admin.getNewName().trim().isEmpty())) {
            throw new IllegalArgumentException("admin new name must not be blank");
        }
        if((admin.getPassword() == null) || (admin.getPassword().trim().isEmpty())) {
            throw new IllegalArgumentException("admin password must not be blank");
        }

        // trim admin name and password before use
        String adminOldName = admin.getOldName().trim();
        String adminNewName = admin.getNewName().trim();
        String adminPassword = admin.getPassword().trim();

        Admin foundAdmin = adminRepository.findByName(adminOldName)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with name: " + adminOldName));

        if(!foundAdmin.getPassword().equals(adminPassword)) {
            throw new WrongPasswordException("Invalid credentials");
        }
        if(adminRepository.existsByName(adminNewName)) {
            throw new java.lang.IllegalArgumentException("admin with name "+ "\'" + adminNewName + "\'" + " already exists");
        }

        foundAdmin.setName(adminNewName);
        Admin savedAdmin = adminRepository.save(foundAdmin);
        return new AdminDTO(savedAdmin.getId(), savedAdmin.getName());
    }

    @Transactional
    public AdminDTO updateAdminPassword(@NonNull AdminPasswordUpdateDTO adminpass) {
        if((adminpass.getName() == null) || (adminpass.getName().trim().isEmpty())) {
            throw new IllegalArgumentException("admin name must not be blank");
        }
        if((adminpass.getPasswordPre() == null) || (adminpass.getPasswordPre().trim().isEmpty())) {
            throw new IllegalArgumentException("Old password must not be blank");
        }
        if((adminpass.getPasswordNew() == null) || (adminpass.getPasswordNew().trim().isEmpty())) {
            throw new IllegalArgumentException("New password must not be blank");
        }

        // trim name and password before using
        String adminName = adminpass.getName().trim();
        String adminPassPre = adminpass.getPasswordPre().trim();
        String adminPassNew = adminpass.getPasswordNew().trim();

        // check if old and new password is same
        if(adminPassPre.equals(adminPassNew)) {
            throw new IllegalArgumentException("New password must be different from old password");
        }

        // get the admin entity
        Admin foundAdmin = adminRepository.findByName(adminName)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with name: " + adminName));

        // verify password
        if(!foundAdmin.getPassword().equals(adminPassPre)) {
            throw new WrongPasswordException("Invalid credentials");
        }
        foundAdmin.setPassword(adminPassNew);
        Admin saveRes = adminRepository.save(foundAdmin);
        return new AdminDTO(saveRes.getId(), saveRes.getName());
    }

    @Transactional
    public AdminDTO deleteAdmin(@NonNull Admin admin) {
        if(admin.getName() == null || admin.getName().trim().isEmpty()) {
            return null;
        } else if(admin.getPassword() == null || admin.getPassword().trim().isEmpty()) {
            return null;
        }

        // trim admin name and password before using
        String adminName = admin.getName().trim();
        String adminPassword = admin.getPassword().trim();

        Admin foundAdmin = adminRepository.findByName(adminName)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with name: " + adminName));

        if(!foundAdmin.getPassword().equals(adminPassword)) {
            throw new WrongPasswordException("Invalid credentials");
        }
        adminRepository.delete(foundAdmin);
        return new AdminDTO(admin.getId(), admin.getName());
    }
}