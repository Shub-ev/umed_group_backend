package com.ug.ug_inventory_management.repositories;

import com.ug.ug_inventory_management.models.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {
}