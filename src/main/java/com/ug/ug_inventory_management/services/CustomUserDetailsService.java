/*
 * JWT filter can inject only one service directly.
 * But, we have 2:
 * 1. AdminServices
 * 2. EmployeeServices
 *
 * And both these services holds "loadUserByUsername()" method
 * Hence we have to create another "unified lookup service" which decides
 * whether the token belongs to and admin or employee and returns the proper
 * UserDetails.
 */

package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.models.Admin;
import com.ug.ug_inventory_management.models.Employee;
import com.ug.ug_inventory_management.repositories.AdminRepository;
import com.ug.ug_inventory_management.repositories.EmployeeRepository;
import com.ug.ug_inventory_management.security.CustomAdminDetails;
import com.ug.ug_inventory_management.security.CustomEmployeeDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsService(AdminRepository adminRepository, EmployeeRepository employeeRepository) {
        this.adminRepository = adminRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<Admin> admin = adminRepository.findByName(username);
        if(admin.isPresent()) {
            return new CustomAdminDetails(admin.get());
        }

        try {
            Long eId = Long.parseLong(username);

            Employee employee = employeeRepository.findByeId(eId)
                    .orElseThrow(() ->
                            new UsernameNotFoundException("User not found"));

            return new CustomEmployeeDetails(employee);

        } catch (NumberFormatException ex) {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
