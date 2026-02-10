package com.ug.ug_inventory_management.repositories;

import com.ug.ug_inventory_management.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
}
