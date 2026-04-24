package online.umedgroup.ug_inventory_management.common.events;

import online.umedgroup.ug_inventory_management.models.InventoryLog;

public class InventoryAuditEvent {

    private final InventoryLog log;

    public InventoryAuditEvent(InventoryLog log) {
        this.log = log;
    }

    public InventoryLog getLog() {
        return log;
    }
}