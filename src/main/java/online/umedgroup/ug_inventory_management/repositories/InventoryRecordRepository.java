//package com.ug.ug_inventory_management.repositories;
//
//import com.ug.ug_inventory_management.models.InventoryRecord;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface InventoryRecordRepository extends JpaRepository<InventoryRecord, Long> {
//    List<InventoryRecord> findByTemplate_IdAndUnitName(Long templateId, String unit_name);
//    List<InventoryRecord> findByTemplate_Id(Long templateId);
//}

package online.umedgroup.ug_inventory_management.repositories;
import online.umedgroup.ug_inventory_management.models.Template;
import org.springframework.data.jpa.repository.Modifying;
import online.umedgroup.ug_inventory_management.models.InventoryRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRecordRepository extends JpaRepository<InventoryRecord, Long> {
    List<InventoryRecord> findByTemplate_IdAndUnitName(Long templateId, String unitName);
    List<InventoryRecord> findByTemplate_Id(Long templateId);
    boolean existsByRecordHash(String hash);
    List<InventoryRecord> findAllById(Iterable<Long> ids);

    @Modifying
    @Transactional
    @Query("DELETE FROM InventoryRecord r WHERE r.template.id = :templateId")
    void deleteByTemplateId(@Param("templateId") Long templateId);
}