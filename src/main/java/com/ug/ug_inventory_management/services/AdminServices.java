package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.common.dtos.Admin.*;
import com.ug.ug_inventory_management.common.exceptions.AdminNotFoundException;
import com.ug.ug_inventory_management.common.exceptions.IllegalArgumentException;
import com.ug.ug_inventory_management.common.exceptions.WrongPasswordException;
import com.ug.ug_inventory_management.models.Admin;
import com.ug.ug_inventory_management.repositories.AdminRepository;
import com.ug.ug_inventory_management.security.JwtService;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @Service marks this class as SprintBean.
@Service
public class AdminServices {

    // Dependency injection by Constructor Injection
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AdminServices(AdminRepository adminRepository,PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AdminResponseDTO createAdmin(@NonNull AdminDTO adminDTO) {
        if (adminDTO.getName() == null || adminDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Admin name must not be blank");
        }

        if (adminDTO.getPassword() == null || adminDTO.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Admin password must not be blank");
        }

        String trimmedName = adminDTO.getName().trim();
        String trimmedPassword = adminDTO.getPassword().trim();
        if(trimmedPassword.length() <= 4 || trimmedPassword.length() >= 15){
            throw new IllegalArgumentException("Password must be 5 to  14 characters");
        }

        if (adminRepository.existsByName(trimmedName)) {
            throw new IllegalArgumentException("Admin already exists with name: " + trimmedName);
        }

        //Hash password before saving to database
        String encodedPassword = passwordEncoder.encode(trimmedPassword);

        Admin admin = new Admin(trimmedName, encodedPassword);

        Admin repoResponse = adminRepository.save(admin);

        return new AdminResponseDTO(repoResponse.getId(), repoResponse.getName());
    }

    public AdminLoginResponseDTO loginAdmin(@NonNull AdminDTO admin) {

        if (admin.getName() == null || admin.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Admin name must not be blank");
        }

        if (admin.getPassword() == null || admin.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Admin password must not be blank");
        }

        String trimmedName = admin.getName().trim();
        String trimmedPassword = admin.getPassword().trim();

        Admin foundAdmin = adminRepository.findByName(trimmedName)
                .orElseThrow(() ->
                        new AdminNotFoundException("Admin not found with name: " + trimmedName)
                );

        //checking if the password matches with the hashed one or not
        if (!passwordEncoder.matches(trimmedPassword, foundAdmin.getPassword())) {
            throw new WrongPasswordException("Invalid credentials");
        }

        // generate JWT token
        String token = jwtService.generateToken(
                admin.getName(),
                "ROLE_ADMIN"
        );

        return new AdminLoginResponseDTO(foundAdmin.getId(), foundAdmin.getName(), token);
    }

    @Transactional
    public AdminResponseDTO updateAdminName(@NonNull AdminNameUpdateDTO admin) {
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

        if(!passwordEncoder.matches(adminPassword, foundAdmin.getPassword())) {
            throw new WrongPasswordException("Invalid credentials");
        }
        if(adminRepository.existsByName(adminNewName)) {
            throw new java.lang.IllegalArgumentException("admin with name "+ "\'" + adminNewName + "\'" + " already exists");
        }

        foundAdmin.setName(adminNewName);
        Admin savedAdmin = adminRepository.save(foundAdmin);
        return new AdminResponseDTO(savedAdmin.getId(), savedAdmin.getName());
    }

    @Transactional
    public AdminResponseDTO updateAdminPassword(@NonNull AdminPasswordUpdateDTO adminpass) {
        if((adminpass.getName() == null) || (adminpass.getName().trim().isEmpty())) {
            throw new IllegalArgumentException("admin name must not be blank");
        }
        if((adminpass.getPasswordPre() == null) || (adminpass.getPasswordPre().trim().isEmpty())) {
            throw new IllegalArgumentException("Old password must not be blank");
        }
        if((adminpass.getPasswordNew() == null) || (adminpass.getPasswordNew().trim().isEmpty())) {
            throw new IllegalArgumentException("New password must not be blank");
        }
        String adminName = adminpass.getName().trim();
        String adminPassPre = adminpass.getPasswordPre().trim();
        String adminPassNew = adminpass.getPasswordNew().trim();

        if(adminPassNew.length() <=5 || adminPassNew.length() >=14){
            throw new IllegalArgumentException("Password must be 5 to 14 characters");
        }


        // check if old and new password is same
        if(adminPassPre.equals(adminPassNew)) {
            throw new IllegalArgumentException("New password must be different from old password");
        }

        // get the admin entity
        Admin foundAdmin = adminRepository.findByName(adminName)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with name: " + adminName));

        // verify password
        if(!passwordEncoder.matches(adminPassPre, foundAdmin.getPassword())) {
            throw new WrongPasswordException("Invalid credentials");
        }
        foundAdmin.setPassword(adminPassNew);
        Admin saveRes = adminRepository.save(foundAdmin);
        return new AdminResponseDTO(saveRes.getId(), saveRes.getName());
    }

    @Transactional
    public AdminResponseDTO deleteAdmin(@NonNull AdminDTO adminDTO) {
        if(adminDTO.getName() == null || adminDTO.getName().trim().isEmpty()) {
            return null;
        }
        if(adminDTO.getPassword() == null || adminDTO.getPassword().trim().isEmpty()) {
            return null;
        }

        // trim admin name and password before using
        String adminName = adminDTO.getName().trim();
        String adminPassword = adminDTO.getPassword().trim();

        Admin foundAdmin = adminRepository.findByName(adminName)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with name: " + adminName));

        if(!foundAdmin.getPassword().equals(adminPassword)) {
            throw new WrongPasswordException("Invalid credentials");
        }
        adminRepository.delete(foundAdmin);
        return new AdminResponseDTO(adminDTO.getId(), adminDTO.getName());
    }

    public AdminResponseDTO convertDTO(Admin admin) {
        return new AdminResponseDTO(admin.getId(), admin.getName());
    }

    public List<AdminResponseDTO> getAdmins() {
        List<Admin> admins = adminRepository.findAll();

        return admins.stream()
                .map(this::convertDTO)
                .toList();
    }

    public Long getAdminCount() {
        return adminRepository.count();
    }

}