package com.ug.ug_inventory_management.repositories;

import java.util.List;

import com.ug.ug_inventory_management.enums.ActionType;
import com.ug.ug_inventory_management.models.InventoryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {

    @Query("""
    SELECT l FROM InventoryLog l
    WHERE (:unitName IS NULL OR l.unitName = :unitName)
    AND (:templateName IS NULL OR LOWER(l.templateName) LIKE LOWER(CONCAT('%', :templateName, '%')))
    AND (:action IS NULL OR l.action = :action)
""")
    Page<InventoryLog> findFilteredLogs(
            @Param("unitName") String unitName,
            @Param("templateName") String templateName,
            @Param("action") ActionType action,
            Pageable pageable
    );


    @Query("""
    SELECT l FROM InventoryLog l
    WHERE l.performedBy = :eId
    AND (:templateName IS NULL OR LOWER(l.templateName) LIKE LOWER(CONCAT('%', :templateName, '%')))
    AND (:action IS NULL OR l.action = :action)
""")
    Page<InventoryLog> findEmployeeLogs(
            @Param("eId") Long eId,
            @Param("templateName") String templateName,
            @Param("action") ActionType action,
            Pageable pageable
    );


    @Query("SELECT DISTINCT r.unitName FROM InventoryRecord r")
    List<String> findAllUnits();
}