package com.ug.ug_inventory_management.repositories;

import com.ug.ug_inventory_management.models.InventoryValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InventoryValueRepository extends JpaRepository<InventoryValue, Long> {

//    #### We have added table joining

//    List<InventoryValue> findByRecordId(Long recordId);

    @Query("""
SELECT v FROM InventoryValue v
JOIN InventoryRecord r ON v.recordId = r.id
JOIN TemplateField f ON v.fieldId = f.id
WHERE r.templateId = :templateId
  AND r.unitId = :unitId
  AND LOWER(f.fieldName) LIKE LOWER(CONCAT('%', :fieldName, '%'))
""")
    List<InventoryValue> findFieldByName(Long templateId, Long unitId, String fieldName);


    List<InventoryValue> findByInventoryRecord_Id(Long recordId);
}




