package online.umedgroup.ug_inventory_management.repositories;

import online.umedgroup.ug_inventory_management.models.InventoryValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface StockAlertRepository extends JpaRepository<InventoryValue, Long> {

    @Query(value = """
        SELECT 
            r.unit_name,
            mf.value AS main_field_value,
            SUM(CAST(sf.value AS SIGNED)) AS total_stock
        FROM inventory_records r
        JOIN templates t ON t.id = r.template_id
        JOIN inventory_values mf ON mf.inventory_record_id = r.id
        JOIN inventory_values sf ON sf.inventory_record_id = r.id
        JOIN template_fields tf1 ON tf1.id = mf.field_id
        JOIN template_fields tf2 ON tf2.id = sf.field_id
        WHERE 
            LOWER(tf1.field_name) = LOWER(t.main_field)
            AND LOWER(tf2.field_name) LIKE CONCAT('%', :stockField, '%')
        GROUP BY r.unit_name, mf.value
        HAVING total_stock < :threshold
    """, nativeQuery = true)
    List<Object[]> findLowStockGrouped(@Param("stockField") String stockField,
                                       @Param("threshold") int threshold);
}