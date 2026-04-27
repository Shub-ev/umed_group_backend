package online.umedgroup.ug_inventory_management.repositories;

import java.util.List;

import online.umedgroup.ug_inventory_management.enums.ActionType;
import online.umedgroup.ug_inventory_management.models.InventoryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import online.umedgroup.ug_inventory_management.models.InventoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {

    @Query("""
        SELECT l FROM InventoryLog l
        WHERE (:unitName IS NULL OR l.unitName = :unitName)
          AND (:mainFieldValue IS NULL OR LOWER(l.mainFieldValue) LIKE LOWER(CONCAT('%', :mainFieldValue, '%')))
          AND (:action IS NULL OR l.action = :action)
    """)
    Page<InventoryLog> findFilteredLogs(
            @Param("unitName") String unitName,
            @Param("mainFieldValue") String mainFieldValue,
            @Param("action") ActionType action,
            Pageable pageable
    );

    @Query("""
        SELECT l FROM InventoryLog l
        WHERE l.performedBy = :eId
          AND (:mainFieldValue IS NULL OR LOWER(l.mainFieldValue) LIKE LOWER(CONCAT('%', :mainFieldValue, '%')))
          AND (:action IS NULL OR l.action = :action)
    """)
    Page<InventoryLog> findEmployeeLogs(
            @Param("eId") Long eId,
            @Param("mainFieldValue") String mainFieldValue,
            @Param("action") ActionType action,
            Pageable pageable
    );

    @Query("SELECT DISTINCT r.unitName FROM InventoryRecord r")
    List<String> findAllUnits();
}