package com.ug.ug_inventory_management.repositories;
import java.util.List;
import com.ug.ug_inventory_management.models.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

//public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {
//    List<InventoryLog> findByUnitNameOrderByCreatedAtDesc(String unitName);
//    List<InventoryLog> findByUnitNameAndTemplateIdOrderByCreatedAtDesc(String unitName, Long templateId);
//}

//@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {

    List<InventoryLog> findByUnitNameOrderByCreatedAtDesc(String unitName);

    List<InventoryLog> findByUnitNameAndTemplateIdOrderByCreatedAtDesc(String unitName, Long templateId);

    @Query("SELECT DISTINCT r.unitName FROM InventoryRecord r")
    List<String> findAllUnits();

}