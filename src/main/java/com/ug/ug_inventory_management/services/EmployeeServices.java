package com.ug.ug_inventory_management.services;


import com.ug.ug_inventory_management.common.dtos.EmployeeDTO;
import  com.ug.ug_inventory_management.common.dtos.EmployeePasswordUpdateDTO;

import com.ug.ug_inventory_management.models.Employee;
import com.ug.ug_inventory_management.repositories.EmployeeRepository;
import org.jspecify.annotations.NonNull;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class EmployeeServices {

    // Dependency Injection using "constructor injection"
    private final EmployeeRepository employeeRepository;
    public EmployeeServices(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public EmployeeDTO createEmployee(@NonNull Employee employee){
        if(employee.getEid()==null){
            return null;
        } else if (employee.getUnit_name()==null ||employee.getUnit_name().trim().isEmpty()) {
            return  null;
        }
        employee.setAllocation(LocalDate.now());
        Employee repoResponse = employeeRepository.save(employee);

        EmployeeDTO employeeDTO=new EmployeeDTO(repoResponse.getEid(),repoResponse.getUnit_name(),repoResponse.getAllocation());
        return employeeDTO;
    }

    public  EmployeeDTO loginEmployee(@NonNull Employee employee){
        if(employee.getEid()==null){
            return  null;
        } else if (employee.getPassword()==null ||employee.getPassword().trim().isEmpty()) {
            return  null;
        }

        Optional<Employee> repoResponse = employeeRepository.findById(employee.getEid());
        if(repoResponse.isPresent()) {
            Employee foundEmployee = repoResponse.get();

            // check the password
            if(employee.getPassword().equals(foundEmployee.getPassword())) {
                EmployeeDTO employeeDTO= new EmployeeDTO(foundEmployee.getEid(), foundEmployee.getUnit_name(),foundEmployee.getAllocation());
                return employeeDTO;
            } else {
                return null;
            }
        } else {
            // return appropriate server response
            return null;
        }
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
        Optional<Employee> repoResponse = employeeRepository.findById(employee.getEid());
        if(repoResponse.isPresent()) {
            Employee foundEmployee = repoResponse.get();

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
        Optional<Employee> repoResponse = employeeRepository.findById(employeepass.getEid());
        if(repoResponse.isPresent()) {
            Employee foundEmployee = repoResponse.get();

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
        } else {
            return null;
        }

    }

}
