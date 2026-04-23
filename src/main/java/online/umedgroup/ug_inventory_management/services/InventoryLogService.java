package online.umedgroup.ug_inventory_management.services;

import online.umedgroup.ug_inventory_management.models.InventoryLog;
import online.umedgroup.ug_inventory_management.repositories.InventoryLogRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryLogService {

    private final InventoryLogRepository repository;

    public InventoryLogService(InventoryLogRepository repository) {
        this.repository = repository;
    }

    public void saveAuditLog(InventoryLog log) {
        try {
            repository.save(log);
        } catch (Exception e) {
            System.err.println("❌ Failed to save audit log: " + e.getMessage());
        }
    }
}