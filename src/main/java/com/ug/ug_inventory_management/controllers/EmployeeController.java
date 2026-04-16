package com.ug.ug_inventory_management.controllers;

import com.ug.ug_inventory_management.common.dtos.Employee.*;
import com.ug.ug_inventory_management.services.EmployeeServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    // Dependency injection using constructor injection.
    private final EmployeeServices employeeServices;
    public EmployeeController(EmployeeServices employeeServices) {
        this.employeeServices = employeeServices;
    }

    @PostMapping("/")
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@RequestBody CreateEmployeeDTO createEmployeeDTO) {
        log.info("Creating employee with Id: {}", createEmployeeDTO.getEId());
        EmployeeResponseDTO employeeResponseDTO = employeeServices.createEmployee(createEmployeeDTO);
        log.info("Created Employee: {}", employeeResponseDTO);

        return ResponseEntity.ok(employeeResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<EmployeeLoginResponseDTO> loginEmployee(@RequestBody LoginEmployeeDTO loginEmployeeDTO) {
        log.info("Login with employee ID: {}", loginEmployeeDTO.getEId());
        EmployeeLoginResponseDTO employeeResponseDTO = employeeServices.loginEmployee(loginEmployeeDTO);
        log.info("Login process completed, response: {}", employeeResponseDTO);

        return ResponseEntity.ok(employeeResponseDTO);
    }

    @PatchMapping("/update/unit_name")
    public ResponseEntity<EmployeeResponseDTO> updateEmployeeUnitName(@RequestBody EmployeeUnitNameUpdateDTO employeeUnitNameUpdateDTO) {
        log.info("Updating unit name for employee: {}", employeeUnitNameUpdateDTO.geteId());
        EmployeeResponseDTO employeeResponseDTO = employeeServices.updateEmployeeUnitName(employeeUnitNameUpdateDTO);
        log.info("Unit name updated successfully for employee: {}", employeeUnitNameUpdateDTO.geteId());

        return ResponseEntity.ok(employeeResponseDTO);
    }

    @PatchMapping("/update/password")
    public ResponseEntity<EmployeeResponseDTO> updateEmployeePassword(@RequestBody EmployeePasswordUpdateDTO employeePasswordUpdateDTO) {
        log.info("Updating password for employee ID: {}", employeePasswordUpdateDTO.getEId());
        EmployeeResponseDTO employeeDTO = employeeServices.updateEmployeePassword(employeePasswordUpdateDTO);
        log.info("Password updated successfully for employee: {}", employeePasswordUpdateDTO.getEId());

        return ResponseEntity.ok(employeeDTO);
    }

    // Delete employee by employee himself
    @DeleteMapping("/delete")
    public ResponseEntity<EmployeeResponseDTO> deleteEmployee(@RequestBody LoginEmployeeDTO loginEmployeeDTO) {
        log.info("Deleting employee with ID: {}", loginEmployeeDTO.getEId());
        EmployeeResponseDTO employeeResponseDTO = employeeServices.deleteEmployee(loginEmployeeDTO);
        log.info("Employee deleted successfully with ID: {}", loginEmployeeDTO.getEId());

        return ResponseEntity.ok(employeeResponseDTO);
    }

    // Delete employee by Admin (no need of password)
    @DeleteMapping("/delete-admin")
    public ResponseEntity<EmployeeResponseDTO> deleteEmployeeByAdmin(@RequestBody LoginEmployeeDTO loginEmployeeDTO) {
        log.info("Deleting employee with ID (by admin): {}", loginEmployeeDTO.getEId());
        EmployeeResponseDTO employeeResponseDTO = employeeServices.deleteEmployeeByAdmin(loginEmployeeDTO);
        log.info("Employee deleted successfully with ID (by admin): {}", loginEmployeeDTO.getEId());

        return ResponseEntity.ok(employeeResponseDTO);
    }

    @GetMapping("/count")
    public Long getEmployeeCount() {
        log.info("Fetching employee count");
        Long employeeCount = employeeServices.employeeCount();
        log.info("Employee count fetched: {}", employeeCount);
        return employeeCount;
    }

    @GetMapping("/get_employees")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployees() {
        log.info("Fetching employees");
        List<EmployeeResponseDTO> employees = employeeServices.getEmployees();
        log.info("Employees fetched");
        return ResponseEntity.ok(employees);
    }
}
