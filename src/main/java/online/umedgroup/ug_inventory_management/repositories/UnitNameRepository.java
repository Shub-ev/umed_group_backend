package online.umedgroup.ug_inventory_management.repositories;

import online.umedgroup.ug_inventory_management.models.UnitName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnitNameRepository extends JpaRepository<UnitName, String> {
    Optional<UnitName> findByUnitName(String unitName);
    void deleteByUnitName(String unitName);
    boolean existsByUnitName(String unitName);
}
