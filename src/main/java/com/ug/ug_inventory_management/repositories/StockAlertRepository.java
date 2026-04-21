package com.ug.ug_inventory_management.repositories;

import com.ug.ug_inventory_management.models.InventoryValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockAlertRepository extends JpaRepository<InventoryValue, Long> {
}