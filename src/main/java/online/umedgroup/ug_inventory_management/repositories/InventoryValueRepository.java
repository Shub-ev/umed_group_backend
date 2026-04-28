package online.umedgroup.ug_inventory_management.repositories;

import online.umedgroup.ug_inventory_management.models.InventoryValue;
import org.springframework.data.jpa.repository.JpaRepository;
import online.umedgroup.ug_inventory_management.models.Template;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InventoryValueRepository extends JpaRepository<InventoryValue, Long> {

//    #### We have added table joining

//    List<InventoryValue> findByRecordId(Long recordId);

    @Query("""
SELECT v FROM InventoryValue v
JOIN v.inventoryRecord r
JOIN TemplateField f ON v.fieldId = f.id
WHERE r.template.id = :templateId
  AND r.unitName = :unitName
  AND LOWER(f.fieldName) LIKE LOWER(CONCAT('%', :fieldName, '%'))
""")
    List<InventoryValue> findFieldByName(Long templateId, String unitName, String fieldName);


    List<InventoryValue> findByInventoryRecord_Id(Long recordId);
    void deleteByInventoryRecord_Id(Long recordId);

    List<InventoryValue> findByFieldIdAndValueIgnoreCase(Long fieldId, String value);
    List<InventoryValue> findByFieldIdAndValueContainingIgnoreCase(Long fieldId, String value);

    List<InventoryValue> findByInventoryRecord_Template_Id(Long templateId);

    @Modifying
    @Transactional
    @Query("""
    DELETE FROM InventoryValue v
    WHERE v.inventoryRecord.template.id = :templateId
    """)
    void deleteByTemplateId(@Param("templateId") Long templateId);
}




