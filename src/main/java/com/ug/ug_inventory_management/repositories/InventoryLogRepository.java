package com.ug.ug_inventory_management.repositories;

import java.util.List;

import com.ug.ug_inventory_management.models.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {

    // Existing
    List<InventoryLog> findByUnitNameOrderByCreatedAtDesc(String unitName);

    List<InventoryLog> findByUnitNameAndTemplateIdOrderByCreatedAtDesc(String unitName, Long templateId);

    List<InventoryLog> findByPerformedByOrderByCreatedAtDesc(Long eId);

    // ✅ NEW METHODS (IMPORTANT)

    // Search by templateName
    List<InventoryLog> findByTemplateNameContainingIgnoreCaseOrderByCreatedAtDesc(String templateName);

    // Search by unit + templateName
    List<InventoryLog> findByUnitNameAndTemplateNameContainingIgnoreCaseOrderByCreatedAtDesc(String unitName, String templateName);

    // Optional: both flexible (OR condition)
    List<InventoryLog> findByUnitNameContainingIgnoreCaseOrTemplateNameContainingIgnoreCaseOrderByCreatedAtDesc(String unitName, String templateName);

    // Units list
    @Query("SELECT DISTINCT r.unitName FROM InventoryRecord r")
    List<String> findAllUnits();
}