package online.umedgroup.ug_inventory_management.repositories;

import online.umedgroup.ug_inventory_management.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByName(String name);
    boolean existsByName(String name);
}