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
    l.main_field_value,

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
    AND (:unit IS NULL OR LOWER(l.unit_name) LIKE LOWER(CONCAT('%', :unit, '%')))
    AND (:mainField IS NULL OR LOWER(l.main_field_value) LIKE LOWER(CONCAT('%', :mainField, '%')))
    AND (:templateId IS NULL OR t.id = :templateId)

GROUP BY 
    l.unit_name, 
    t.id, 
    t.template_name,
    l.main_field_value
""", nativeQuery = true)
    List<Object[]> getMainFieldReport(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("unit") String unit,
            @Param("mainField") String mainField,
            @Param("templateId") Long templateId
    );


    @Query(value = """
    SELECT 
        l.unit_name,
        t.id,
        t.template_name,
        l.main_field_value,
        l.record_id,
        l.action,
        l.change_qty,
        l.created_at
    FROM inventory_audit_logs l
    JOIN templates t ON t.id = l.template_id
    WHERE 
        (:fromDate IS NULL OR l.created_at >= :fromDate)
        AND (:toDate IS NULL OR l.created_at <= :toDate)
        AND (:unit IS NULL OR LOWER(l.unit_name) LIKE LOWER(CONCAT('%', :unit, '%')))
        AND (:templateId IS NULL OR t.id = :templateId)
    ORDER BY l.created_at DESC
""", nativeQuery = true)
    List<Object[]> getDetailReport(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("unit") String unit,
            @Param("templateId") Long templateId
    );
}