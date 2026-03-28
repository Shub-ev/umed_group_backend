package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.common.dtos.EmployeeDTO;
import  com.ug.ug_inventory_management.common.dtos.EmployeePasswordUpdateDTO;

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

    public EmployeeDTO createEmployee(@NonNull Employee employee){
        if(employee.getEId() == null){
            throw new IllegalArgumentException("Employee Id cannot be blank");
        }

        String trimmedUnit = employee.getUnitName();
        if(trimmedUnit == null || trimmedUnit.trim().isEmpty()){
            throw new IllegalArgumentException("Employee unit name cannot be blank");
        }
        trimmedUnit = trimmedUnit.trim();


        String trimmedPassword = employee.getPassword();
        if(trimmedPassword == null || trimmedPassword.trim().isEmpty()){
            throw new IllegalArgumentException("Employee password cannot be blank");
        }
        trimmedPassword = trimmedPassword.trim();
        if(trimmedPassword.length() <= 5 || trimmedPassword.length() >= 14){
            throw new IllegalArgumentException("Password must be 5 to  14 characters");
        }

        // 4️⃣ Check duplicate employee
        if(employeeRepository.existsById(employee.getEId())) {
            throw new IllegalArgumentException("Employee already exists with id: " + employee.getEId());
        }

        // 5️⃣ 🔐 Hash password BEFORE saving
        String hashedPassword = passwordEncoder.encode(trimmedPassword);

        // 6️⃣ Set cleaned & processed values
        employee.setUnitName(trimmedUnit);
        employee.setPassword(hashedPassword);
        employee.setAllocation(LocalDate.now());


        Employee repoResponse = employeeRepository.save(employee);

        EmployeeDTO employeeDTO= new EmployeeDTO(
                repoResponse.getEId(),
                repoResponse.getUnitName(),
                repoResponse.getAllocation()
        );
        return employeeDTO;
    }

    public  EmployeeDTO loginEmployee(@NonNull Employee employee){
        if(employee.getEId()==null){
            throw new IllegalArgumentException("Employee Id can not be blank");
        }
        String rawPassword = employee.getPassword();
        if(rawPassword == null || rawPassword.trim().isEmpty()){
            throw new IllegalArgumentException("Employee password cannot be blank");
        }
        rawPassword = rawPassword.trim();


        Employee foundEmployee = employeeRepository.findByEId(employee.getEId())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with eid: " + employee.getEId()));

        // check the password
        if(!passwordEncoder.matches(rawPassword, foundEmployee.getPassword())) {
            throw new WrongPasswordException("Invalid credentials");
        }
        EmployeeDTO employeeDTO= new EmployeeDTO(
                foundEmployee.getEId(),
                foundEmployee.getUnitName(),
                foundEmployee.getAllocation()
        );
        return employeeDTO;
    }

    public EmployeeDTO updateEmployeeUnitName(@NonNull Employee employee) {
        // Check if admin input have required fields
        if((employee.getEId() == null)) {
            // return appropriate server response
            return null;
        } else if((employee.getPassword() == null) || (employee.getPassword().trim().isEmpty())) {
            // return appropriate server response
            return null;
        }
        Employee foundEmployee = employeeRepository.findById(employee.getEId())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with eid: " + employee.getEId()));

        // verify password before updating the admin entity
        if(foundEmployee.getPassword().equals(employee.getPassword())) {
            foundEmployee.setUnitName(employee.getUnitName());
            Employee saveRes = employeeRepository.save(foundEmployee);
            if (saveRes != null) {
                EmployeeDTO employeeDTO = new EmployeeDTO(saveRes.getEId(), saveRes.getUnitName(),saveRes.getAllocation());
                return employeeDTO;
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    public EmployeeDTO updateEmployeePassword(@NonNull EmployeePasswordUpdateDTO employeepass) {
        // Check if admin input have required fields
        if(employeepass.getEid() == null) {
            throw new IllegalArgumentException("Employee Id cannot be blank");
        }

        String oldPassword = employeepass.getPasswordPre();
        if(oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Old password cannot be blank");
        }
        oldPassword = oldPassword.trim();

        String newPassword = employeepass.getPasswordNew();
        if(newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be blank");
        }
        newPassword = newPassword.trim();


        if(newPassword.length() <= 5 || newPassword.length() >= 14){
            throw new IllegalArgumentException("Password must be 5 to 14 characters");
        }

        Employee foundEmployee = employeeRepository.findById(employeepass.getEid())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with eid: " + employeepass.getEid()));

        // verify password before updating the admin entity
        if(foundEmployee.getPassword().equals(employeepass.getPasswordPre())) {
            foundEmployee.setPassword(employeepass.getPasswordNew());
            Employee saveRes = employeeRepository.save(foundEmployee);
            if (saveRes != null) {
                EmployeeDTO employeeDTO = new EmployeeDTO(saveRes.getEId(), saveRes.getPassword(),saveRes.getAllocation());
                return employeeDTO;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public Long employeeCount() {
        return employeeRepository.count();
    }

    public EmployeeDTO convertDTO(Employee employee) {
        return new EmployeeDTO(
                employee.getEId(), employee.getUnitName(), employee.getAllocation()
        );
    }

    public List<EmployeeDTO> getEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream()
                .map(this::convertDTO)
                .toList();
    }
}
