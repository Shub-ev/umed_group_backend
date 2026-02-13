package com.ug.ug_inventory_management.controllers;

import com.ug.ug_inventory_management.common.dtos.EmployeeDTO;
import com.ug.ug_inventory_management.common.dtos.EmployeePasswordUpdateDTO;
import com.ug.ug_inventory_management.models.Employee;
import com.ug.ug_inventory_management.services.EmployeeServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    // Dependency injection using constructor injection.
    private final EmployeeServices employeeServices;
    public EmployeeController(EmployeeServices employeeServices) {
        this.employeeServices = employeeServices;
    }

    @PostMapping("/")
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody Employee employee) {
        EmployeeDTO employeeDTO = employeeServices.createEmployee(employee);
        System.out.println(employeeDTO);

        return ResponseEntity.ok(employeeDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<EmployeeDTO> loginEmployee(@RequestBody Employee employee) {
        EmployeeDTO employeeDTO = employeeServices.loginEmployee(employee);
        System.out.println(employeeDTO);

        return ResponseEntity.ok(employeeDTO);
    }

    @PatchMapping("/update/unit_name")
    public ResponseEntity<EmployeeDTO> updateEmployeeUnitName(@RequestBody Employee employee) {
        EmployeeDTO employeeDTO = employeeServices.updateEmployeeUnitName(employee);
        System.out.println(employeeDTO);

        return ResponseEntity.ok(employeeDTO);
    }

    @PatchMapping("/update/password")
    public ResponseEntity<EmployeeDTO> updateEmployeePassword(@RequestBody EmployeePasswordUpdateDTO employeePasswordUpdateDTO) {
        EmployeeDTO employeeDTO = employeeServices.updateEmployeePassword(employeePasswordUpdateDTO);
        System.out.println(employeeDTO);

        return ResponseEntity.ok(employeeDTO);
    }
}
