package online.umedgroup.ug_inventory_management.repositories;

import online.umedgroup.ug_inventory_management.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    Optional<Employee> findByeId(Long eId);
}
