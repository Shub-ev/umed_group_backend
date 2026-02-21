package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.common.dtos.EmployeeDTO;
import  com.ug.ug_inventory_management.common.dtos.EmployeePasswordUpdateDTO;

import com.ug.ug_inventory_management.common.exceptions.EmployeeNotFoundException;
import com.ug.ug_inventory_management.common.exceptions.IllegalArgumentException;
import com.ug.ug_inventory_management.common.exceptions.WrongPasswordException;
import com.ug.ug_inventory_management.models.Employee;
import com.ug.ug_inventory_management.repositories.EmployeeRepository;
import org.jspecify.annotations.NonNull;
import com.ug.ug_inventory_management.repositories.EmployeeRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
        if(employee.getEid() == null){
            throw new IllegalArgumentException("Employee Id can not be blank");
        }
        if (employee.getUnit_name() == null || employee.getUnit_name().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee unit name can not be blank");
        }
        if((employee.getPassword() == null) || (employee.getPassword().trim().isEmpty())) {
            throw new IllegalArgumentException("Employee password can not be blank");
        }
        // check if employee with same eid exist
        if(employeeRepository.existsById(employee.getEid())) {
            throw new IllegalArgumentException("Employee already exists with id: " + employee.getEid());
        }


        employee.setAllocation(LocalDate.now());
        // trim unit name and password before saving
        employee.setUnit_name(employee.getUnit_name().trim());
        String trimmedpass = employee.getPassword().trim();
        Employee repoResponse = employeeRepository.save(employee);
        String hashedPassword=passwordEncoder.encode(trimmedpass);
        employee.setPassword(hashedPassword);

        EmployeeDTO employeeDTO= new EmployeeDTO(
                repoResponse.getEid(),
                repoResponse.getUnit_name(),
                repoResponse.getAllocation()
        );
        return employeeDTO;
    }

    public  EmployeeDTO loginEmployee(@NonNull Employee employee){
        if(employee.getEid()==null){
            throw new IllegalArgumentException("Employee Id can not be blank");
        } else if (employee.getPassword()==null ||employee.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee password can not be blank");
        }

        Employee foundEmployee = employeeRepository.findById(employee.getEid())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with eid: " + employee.getEid()));

        // check the password
        if(!employee.getPassword().equals(foundEmployee.getPassword())) {
            throw new WrongPasswordException("Invalid credentials");
        }
        EmployeeDTO employeeDTO= new EmployeeDTO(
                foundEmployee.getEid(),
                foundEmployee.getUnit_name(),
                foundEmployee.getAllocation()
        );
        return employeeDTO;
    }

    public EmployeeDTO updateEmployeeUnitName(@NonNull Employee employee) {
        // Check if admin input have required fields
        if((employee.getEid() == null)) {
            // return appropriate server response
            return null;
        } else if((employee.getPassword() == null) || (employee.getPassword().trim().isEmpty())) {
            // return appropriate server response
            return null;
        }
        Employee foundEmployee = employeeRepository.findById(employee.getEid())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with eid: " + employee.getEid()));

        // verify password before updating the admin entity
        if(foundEmployee.getPassword().equals(employee.getPassword())) {
            foundEmployee.setUnit_name(employee.getUnit_name());
            Employee saveRes = employeeRepository.save(foundEmployee);
            if (saveRes != null) {
                EmployeeDTO employeeDTO = new EmployeeDTO(saveRes.getEid(), saveRes.getUnit_name(),saveRes.getAllocation());
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
        if((employeepass.getEid() == null)) {
            // return appropriate server response
            return null;
        } else if((employeepass.getPasswordPre() == null) || (employeepass.getPasswordPre().trim().isEmpty())) {
            // return appropriate server response
            return null;
        } else if((employeepass.getPasswordNew() == null) || (employeepass.getPasswordNew().trim().isEmpty())) {
            // return appropriate server response
            return null;
        }

        Employee foundEmployee = employeeRepository.findById(employeepass.getEid())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with eid: " + employeepass.getEid()));

        // verify password before updating the admin entity
        if(foundEmployee.getPassword().equals(employeepass.getPasswordPre())) {
            foundEmployee.setPassword(employeepass.getPasswordNew());
            Employee saveRes = employeeRepository.save(foundEmployee);
            if (saveRes != null) {
                EmployeeDTO employeeDTO = new EmployeeDTO(saveRes.getEid(), saveRes.getPassword(),saveRes.getAllocation());
                return employeeDTO;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
