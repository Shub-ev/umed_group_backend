package com.ug.ug_inventory_management.common.listeners;
import org.springframework.scheduling.annotation.Async;
import com.ug.ug_inventory_management.common.events.InventoryAuditEvent;
import com.ug.ug_inventory_management.services.InventoryLogService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class InventoryAuditListener {

    private final InventoryLogService auditService;

    public InventoryAuditListener(InventoryLogService auditService) {
        this.auditService = auditService;
    }


    @Async("auditExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuditEvent(InventoryAuditEvent event) {
        try {
            System.out.println("AUDIT THREAD: " + Thread.currentThread().getName());
            auditService.saveAuditLog(event.getLog());
        } catch (Exception e) {
            e.printStackTrace(); // 🔥 IMPORTANT
        }
    }
}