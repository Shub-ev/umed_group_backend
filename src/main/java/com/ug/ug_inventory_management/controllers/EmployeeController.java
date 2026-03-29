package com.ug.ug_inventory_management.controllers;

import com.ug.ug_inventory_management.common.dtos.Admin.AdminDTO;
import com.ug.ug_inventory_management.common.dtos.Admin.AdminResponseDTO;
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
    public ResponseEntity<ResponseEmployeeDTO> createEmployee(@RequestBody CreateEmployeeDTO createEmployeeDTO) {
        log.info("Creating employee with Id: {}", createEmployeeDTO.getEId());
        ResponseEmployeeDTO responseEmployeeDTO = employeeServices.createEmployee(createEmployeeDTO);
        log.info("Created Employee: {}", responseEmployeeDTO);

        return ResponseEntity.ok(responseEmployeeDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseEmployeeDTO> loginEmployee(@RequestBody LoginEmployeeDTO loginEmployeeDTO) {
        log.info("Login with employee ID: {}", loginEmployeeDTO.getEId());
        ResponseEmployeeDTO responseEmployeeDTO = employeeServices.loginEmployee(loginEmployeeDTO);
        log.info("Login process completed, response: {}", responseEmployeeDTO);

        return ResponseEntity.ok(responseEmployeeDTO);
    }

    @PatchMapping("/update/unit_name")
    public ResponseEntity<ResponseEmployeeDTO> updateEmployeeUnitName(@RequestBody EmployeeUnitNameUpdateDTO employeeUnitNameUpdateDTO) {
        log.info("Updating unit name for employee: {}", employeeUnitNameUpdateDTO.geteId());
        ResponseEmployeeDTO responseEmployeeDTO = employeeServices.updateEmployeeUnitName(employeeUnitNameUpdateDTO);
        log.info("Unit name updated successfully for employee: {}", employeeUnitNameUpdateDTO.geteId());

        return ResponseEntity.ok(responseEmployeeDTO);
    }

    @PatchMapping("/update/password")
    public ResponseEntity<ResponseEmployeeDTO> updateEmployeePassword(@RequestBody EmployeePasswordUpdateDTO employeePasswordUpdateDTO) {
        log.info("Updating password for employee ID: {}", employeePasswordUpdateDTO.geteId());
        ResponseEmployeeDTO employeeDTO = employeeServices.updateEmployeePassword(employeePasswordUpdateDTO);
        log.info("Password updated successfully for employee: {}", employeePasswordUpdateDTO.geteId());

        return ResponseEntity.ok(employeeDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseEmployeeDTO> deleteEmployee(@RequestBody LoginEmployeeDTO loginEmployeeDTO) {
        log.info("Deleting employee with ID: {}", loginEmployeeDTO.getEId());
        ResponseEmployeeDTO responseEmployeeDTO = employeeServices.deleteEmployee(loginEmployeeDTO);
        log.info("Employee deleted successfully with ID: {}", loginEmployeeDTO.getEId());

        return ResponseEntity.ok(responseEmployeeDTO);
    }

    @GetMapping("/count")
    public Long getEmployeeCount() {
        log.info("Fetching employee count");
        Long employeeCount = employeeServices.employeeCount();
        log.info("Employee count fetched: {}", employeeCount);
        return employeeCount;
    }

    @GetMapping("/get_employees")
    public ResponseEntity<List<ResponseEmployeeDTO>> getEmployees() {
        log.info("Fetching employees");
        List<ResponseEmployeeDTO> employees = employeeServices.getEmployees();
        log.info("Employees fetched");
        return ResponseEntity.ok(employees);
    }
}
