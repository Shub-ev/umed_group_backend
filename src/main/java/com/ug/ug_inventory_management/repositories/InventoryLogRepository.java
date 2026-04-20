package com.ug.ug_inventory_management.repositories;

import java.util.List;

import com.ug.ug_inventory_management.enums.ActionType;
import com.ug.ug_inventory_management.models.InventoryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {

    // ✅ Base filters with pagination
    Page<InventoryLog> findByUnitName(String unitName, Pageable pageable);

    Page<InventoryLog> findByTemplateNameContainingIgnoreCase(String templateName, Pageable pageable);

    Page<InventoryLog> findByAction(ActionType action, Pageable pageable);

    // ✅ Combined filters
    Page<InventoryLog> findByUnitNameAndTemplateNameContainingIgnoreCase(
            String unitName, String templateName, Pageable pageable
    );

    Page<InventoryLog> findByUnitNameAndAction(
            String unitName, ActionType action, Pageable pageable
    );

    Page<InventoryLog> findByTemplateNameContainingIgnoreCaseAndAction(
            String templateName, ActionType action, Pageable pageable
    );

    Page<InventoryLog> findByUnitNameAndTemplateNameContainingIgnoreCaseAndAction(
            String unitName, String templateName, ActionType action, Pageable pageable
    );


    // Units list
    @Query("SELECT DISTINCT r.unitName FROM InventoryRecord r")
    List<String> findAllUnits();
}