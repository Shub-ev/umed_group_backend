package com.ug.ug_inventory_management.models;
import com.ug.ug_inventory_management.enums.ActionType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "inventory_audit_logs")
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long templateId;

    private String templateName;

    private String unitName;

    @Enumerated(EnumType.STRING)  // Stores enum as String in database
    private ActionType action;

    private Integer changeQty;
    private Integer previousQty;
    private Integer newQty;



    @Column(name = "performed_by")
    private Long performedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();



    public InventoryLog() {
        // Required by JPA
    }

    public InventoryLog(Long templateId, String unitName, ActionType action,
                        Integer changeQty, Integer previousQty,
                        Integer newQty, Long performedBy,String templateName) {
        this.templateId = templateId;
        this.unitName = unitName;
        this.action = action;
        this.changeQty = changeQty;
        this.previousQty = previousQty;
        this.newQty = newQty;
        this.performedBy = performedBy;
        this.templateName=templateName;
    }

    public Long getId() {
        return id;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public String getUnitName() {
        return unitName;
    }

    public ActionType getAction() {
        return action;
    }

    public Integer getChangeQty() {
        return changeQty;
    }

    public Integer getPreviousQty() {
        return previousQty;
    }

    public Integer getNewQty() {
        return newQty;
    }

    public Long getPerformedBy() {
        return performedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTemplateName() {
        return templateName;
    }
}

