package com.ug.ug_inventory_management.repositories;

import com.ug.ug_inventory_management.models.InventoryValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryValueRepository extends JpaRepository<InventoryValue, Long> {
    List<InventoryValue> findByRecordId(Long recordId);
}