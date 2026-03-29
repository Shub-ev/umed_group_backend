package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.common.dtos.Employee.*;

import com.ug.ug_inventory_management.common.exceptions.EmployeeNotFoundException;
import com.ug.ug_inventory_management.common.exceptions.IllegalArgumentException;
import com.ug.ug_inventory_management.common.exceptions.WrongPasswordException;
import com.ug.ug_inventory_management.models.Employee;
import com.ug.ug_inventory_management.repositories.EmployeeRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeServices {

    // Dependency Injection using "constructor injection"
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServices(EmployeeRepository employeeRepository,PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEmployeeDTO createEmployee(@NonNull CreateEmployeeDTO createEmployeeDTO){
        if(createEmployeeDTO.getEId() == null){
            throw new IllegalArgumentException("Employee Id cannot be blank");
        }

        String unitName = createEmployeeDTO.getUnitName();
        if(unitName == null || unitName.trim().isEmpty()){
            throw new IllegalArgumentException("Employee unit name cannot be blank");
        }
        unitName = unitName.trim();

        String password = createEmployeeDTO.getPassword();
        if(password == null || password.trim().isEmpty()){
            throw new IllegalArgumentException("Employee password cannot be blank");
        }
        password = password.trim();
        if(password.length() <= 5 || password.length() >= 14){
            throw new IllegalArgumentException("Password must be 5 to  14 characters");
        }

        if(employeeRepository.findByeId(createEmployeeDTO.getEId()).isPresent()) {
            throw new IllegalArgumentException("Employee already exists with id: " + createEmployeeDTO.getEId());
        }

        // 5️⃣ 🔐 Hash password BEFORE saving
        String hashedPassword = passwordEncoder.encode(password);

        Employee employee = new Employee(createEmployeeDTO.getEId(), unitName, hashedPassword, LocalDate.now());

        Employee repoResponse = employeeRepository.save(employee);

        ResponseEmployeeDTO responseEmployeeDTO= new ResponseEmployeeDTO(
                repoResponse.getId(),
                repoResponse.getEId(),
                repoResponse.getUnitName(),
                repoResponse.getAllocation()
        );
        return responseEmployeeDTO;
    }

    public ResponseEmployeeDTO loginEmployee(@NonNull LoginEmployeeDTO loginEmployeeDTO){
        if(loginEmployeeDTO.getEId() == null){
            throw new IllegalArgumentException("Employee Id can not be blank");
        }

        String rawPassword = loginEmployeeDTO.getPassword();
        if(rawPassword == null || rawPassword.trim().isEmpty()){
            throw new IllegalArgumentException("Employee password cannot be blank");
        }
        rawPassword = rawPassword.trim();

        Employee foundEmployee = employeeRepository.findByeId(loginEmployeeDTO.getEId())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with eid: " + loginEmployeeDTO.getEId()));

        // check the password
        if(!passwordEncoder.matches(rawPassword, foundEmployee.getPassword())) {
            throw new WrongPasswordException("Invalid credentials");
        }

        ResponseEmployeeDTO responseEmployeeDTO= new ResponseEmployeeDTO(
                foundEmployee.getId(),
                foundEmployee.getEId(),
                foundEmployee.getUnitName(),
                foundEmployee.getAllocation()
        );
        return responseEmployeeDTO;
    }

    public ResponseEmployeeDTO updateEmployeeUnitName(@NonNull EmployeeUnitNameUpdateDTO employeeUnitNameUpdateDTO) {

        if (employeeUnitNameUpdateDTO.geteId() == null) {
            throw new IllegalArgumentException("Employee Id cannot be null");
        }
        if (employeeUnitNameUpdateDTO.getPassword() == null || employeeUnitNameUpdateDTO.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (employeeUnitNameUpdateDTO.getOldUnitName() == null || employeeUnitNameUpdateDTO.getOldUnitName().trim().isEmpty()) {
            throw new IllegalArgumentException("Old unit name cannot be empty");
        }
        if (employeeUnitNameUpdateDTO.getNewUnitName() == null || employeeUnitNameUpdateDTO.getNewUnitName().trim().isEmpty()) {
            throw new IllegalArgumentException("Old unit name cannot be empty");
        }

        Employee foundEmployee = employeeRepository.findByeId(employeeUnitNameUpdateDTO.geteId())
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found with eid: " + employeeUnitNameUpdateDTO.geteId())
                );

        // ✅ FIX: use passwordEncoder
        if (!passwordEncoder.matches(employeeUnitNameUpdateDTO.getPassword(), foundEmployee.getPassword())) {
            throw new WrongPasswordException("Invalid password");
        }

        // ✅ trim and update
        foundEmployee.setUnitName(employeeUnitNameUpdateDTO.getNewUnitName().trim());

        Employee saved = employeeRepository.save(foundEmployee);

        return new ResponseEmployeeDTO(
                saved.getId(),
                saved.getEId(),
                saved.getUnitName(),
                saved.getAllocation()
        );
    }

    public ResponseEmployeeDTO updateEmployeePassword(@NonNull EmployeePasswordUpdateDTO employeePasswordUpdateDTO) {

        if (employeePasswordUpdateDTO.geteId() == null) {
            throw new IllegalArgumentException("Employee Id cannot be blank");
        }

        String oldPassword = employeePasswordUpdateDTO.getPasswordPre();
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Old password cannot be blank");
        }

        String newPassword = employeePasswordUpdateDTO.getPasswordNew();
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be blank");
        }

        if(newPassword.equals(oldPassword)) {
            throw new IllegalArgumentException("New password can not be same as old");
        }

        if (newPassword.length() <= 5 || newPassword.length() >= 14) {
            throw new IllegalArgumentException("Password must be 5 to 14 characters");
        }

        Employee foundEmployee = employeeRepository.findByeId(employeePasswordUpdateDTO.geteId())
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found with eid: " + employeePasswordUpdateDTO.geteId())
                );

        // ✅ FIX: compare hashed password
        if (!passwordEncoder.matches(oldPassword, foundEmployee.getPassword())) {
            throw new WrongPasswordException("Invalid old password");
        }

        // ✅ FIX: encode new password
        String encodedPassword = passwordEncoder.encode(newPassword);
        foundEmployee.setPassword(encodedPassword);

        Employee saved = employeeRepository.save(foundEmployee);

        return new ResponseEmployeeDTO(
                saved.getId(),
                saved.getEId(),
                saved.getUnitName(),
                saved.getAllocation()
        );
    }

    public ResponseEmployeeDTO deleteEmployee(@NonNull LoginEmployeeDTO loginEmployeeDTO) {
        if(loginEmployeeDTO.getEId() == null){
            throw new IllegalArgumentException("Employee Id can not be blank");
        }

        String rawPassword = loginEmployeeDTO.getPassword();
        if(rawPassword == null || rawPassword.trim().isEmpty()){
            throw new IllegalArgumentException("Employee password cannot be blank");
        }
        rawPassword = rawPassword.trim();

        Employee foundEmployee = employeeRepository.findByeId(loginEmployeeDTO.getEId())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with eid: " + loginEmployeeDTO.getEId()));

        // check the password
        if(!passwordEncoder.matches(rawPassword, foundEmployee.getPassword())) {
            throw new WrongPasswordException("Invalid credentials");
        }

        employeeRepository.delete(foundEmployee);
        return new ResponseEmployeeDTO(
                foundEmployee.getId(),
                foundEmployee.getEId(),
                foundEmployee.getUnitName(),
                foundEmployee.getAllocation()
        );
    }

    public Long employeeCount() {
        return employeeRepository.count();
    }

    public ResponseEmployeeDTO convertDTO(Employee employee) {
        return new ResponseEmployeeDTO(
                employee.getId(),
                employee.getEId(), employee.getUnitName(), employee.getAllocation()
        );
    }

    public List<ResponseEmployeeDTO> getEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream()
                .map(this::convertDTO)
                .toList();
    }
}
