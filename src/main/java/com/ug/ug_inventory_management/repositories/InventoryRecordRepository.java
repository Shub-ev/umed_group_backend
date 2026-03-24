package com.ug.ug_inventory_management.repositories;

import com.ug.ug_inventory_management.models.InventoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface InventoryRecordRepository extends JpaRepository<InventoryRecord, Long> {
    List<InventoryRecord> findByTemplateIdAndUnitId(Long templateId, Long unitId);
    List<InventoryRecord> findByTemplateId(Long templateId);
}