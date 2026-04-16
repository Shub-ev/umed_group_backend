package com.ug.ug_inventory_management.repositories;
import java.util.List;
import com.ug.ug_inventory_management.models.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

//@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {
    List<InventoryLog> findByUnitNameOrderByCreatedAtDesc(String unitName);

    List<InventoryLog> findByUnitNameAndTemplateIdOrderByCreatedAtDesc(String unitName, Long templateId);

    List<InventoryLog> findByPerformedByOrderByCreatedAtDesc(Long eId);

    @Query("SELECT DISTINCT r.unitName FROM InventoryRecord r")
    List<String> findAllUnits();

}