package online.umedgroup.ug_inventory_management.repositories;

import online.umedgroup.ug_inventory_management.models.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<InventoryLog, Long> {

    @Query(value = """
        SELECT 
            l.unit_name,
            t.id,
            t.template_name,
            SUM(CASE WHEN l.action = 'INWARD' THEN l.change_qty ELSE 0 END),
            SUM(CASE WHEN l.action = 'OUTWARD' THEN l.change_qty ELSE 0 END),
            SUM(CASE 
                WHEN l.action = 'INWARD' THEN l.change_qty 
                WHEN l.action = 'OUTWARD' THEN -l.change_qty 
                ELSE 0 
            END)
        FROM inventory_audit_logs l
        JOIN templates t ON t.id = l.template_id
        WHERE 
            (:fromDate IS NULL OR l.created_at >= :fromDate)
            AND (:toDate IS NULL OR l.created_at <= :toDate)

            -- partial match for unit
            AND (:unit IS NULL OR LOWER(l.unit_name) LIKE LOWER(CONCAT('%', :unit, '%')))

            -- strict match for ID
            AND (:templateId IS NULL OR t.id = :templateId)

        GROUP BY l.unit_name, t.id, t.template_name
    """, nativeQuery = true)
    List<Object[]> getReport(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("unit") String unit,
            @Param("templateId") Long templateId
    );
}