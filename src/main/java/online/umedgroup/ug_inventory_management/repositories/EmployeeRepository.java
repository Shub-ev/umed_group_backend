package online.umedgroup.ug_inventory_management.repositories;

import online.umedgroup.ug_inventory_management.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    Optional<Employee> findByeId(Long eId);
    @Query("SELECT e FROM Employee e WHERE e.eId IN :eIds")
    List<Employee> findAllByeId(@Param("eIds") Iterable<Long> eIds);
}
