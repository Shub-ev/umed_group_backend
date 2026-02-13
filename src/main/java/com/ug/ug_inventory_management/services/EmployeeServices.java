package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.repositories.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServices {

    // Dependency Injection using "constructor injection"
    private final EmployeeRepository employeeRepository;

    public EmployeeServices(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
}
