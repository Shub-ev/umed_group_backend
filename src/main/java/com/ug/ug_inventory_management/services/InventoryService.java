//package com.ug.ug_inventory_management.services;

package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.common.events.InventoryAuditEvent;
import com.ug.ug_inventory_management.models.InventoryLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import com.ug.ug_inventory_management.common.dtos.CreateInventoryRecordDTO;
import com.ug.ug_inventory_management.models.InventoryRecord;
import com.ug.ug_inventory_management.models.InventoryValue;
import com.ug.ug_inventory_management.models.Template;
import com.ug.ug_inventory_management.models.TemplateField;
import com.ug.ug_inventory_management.repositories.InventoryRecordRepository;
import com.ug.ug_inventory_management.repositories.InventoryValueRepository;
import com.ug.ug_inventory_management.repositories.TemplateFieldRepository;
import com.ug.ug_inventory_management.common.dtos.InventoryUpdateRecordDTO;
import com.ug.ug_inventory_management.repositories.TemplateRepository;
import com.ug.ug_inventory_management.enums.ActionType;
import com.ug.ug_inventory_management.repositories.InventoryLogRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRecordRepository inventoryRecordRepository;
    private final InventoryValueRepository inventoryValueRepository;
    private final TemplateFieldRepository templateFieldRepository;
    private final TemplateRepository templateRepository;
    private final ApplicationEventPublisher publisher;
    private final InventoryLogRepository inventoryLogRepository;

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    public InventoryService(InventoryRecordRepository inventoryRecordRepository,
                            InventoryValueRepository inventoryValueRepository,
                            TemplateFieldRepository templateFieldRepository,
                            TemplateRepository templateRepository,
                            ApplicationEventPublisher publisher,
                            InventoryLogRepository inventoryLogRepository) {
        this.inventoryRecordRepository = inventoryRecordRepository;
        this.inventoryValueRepository = inventoryValueRepository;
        this.templateFieldRepository = templateFieldRepository;
        this.templateRepository = templateRepository;
        this.publisher = publisher;
        this.inventoryLogRepository = inventoryLogRepository;
    }

    @Transactional
    public void addInventory(@NotNull CreateInventoryRecordDTO request) {

        Template template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new RuntimeException("Template not found"));

        InventoryRecord inventoryRecord = new InventoryRecord(template, request.getUnitName());
        inventoryRecordRepository.save(inventoryRecord);

        List<TemplateField> fields =
                templateFieldRepository.findByTemplate_IdOrderByDisplayOrderAsc(request.getTemplateId());

        Map<String, String> requestValues =
                request.getValues() != null ? request.getValues() : new HashMap<>();

        InventoryValue inwardField = null;
        InventoryValue outwardField = null;
        InventoryValue stockField = null;
        List<InventoryValue> savedValues = new ArrayList<>();

        for (TemplateField field : fields) {

            String value = requestValues.get(field.getFieldName());
            String fieldNameLower = field.getFieldName().toLowerCase();

            if (value == null) {
                if (fieldNameLower.equals("inward") ||
                        fieldNameLower.equals("outward") ||
                        fieldNameLower.equals("stock")) {
                    value = "0";
                } else {
                    value = "";
                }
            }

            InventoryValue inventoryValue =
                    new InventoryValue(inventoryRecord, field.getId(), value);

            savedValues.add(inventoryValue);

            if (fieldNameLower.contains("inward")) inwardField = inventoryValue;
            else if (fieldNameLower.contains("outward")) outwardField = inventoryValue;
            else if (fieldNameLower.contains("stock")) stockField = inventoryValue;
        }

        inventoryValueRepository.saveAll(savedValues);

        // LOG CREATION
        if (inwardField != null && outwardField != null && stockField != null) {

            int inward = safeParse(inwardField.getValue());
            int outward = safeParse(outwardField.getValue());

            int previousStock = inward - outward;
            int newStock = inward - outward;

            InventoryLog inventoryLog = new InventoryLog(
                    request.getTemplateId(),
                    request.getUnitName(),
                    ActionType.INWARD,
                    inward,
                    previousStock,
                    newStock,
                    request.getEId(),
                    template.getTemplateName() != null ? template.getTemplateName() : "-"
            );

            log.info("Publishing inventory audit event for new inventory");
            publisher.publishEvent(new InventoryAuditEvent(inventoryLog));
        }
    }

    private int safeParse(String value) {
        try {
            return (value == null || value.trim().isEmpty())
                    ? 0
                    : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public List<Map<String, String>> getInventorySummary(Long templateId) {

        List<InventoryRecord> records =
                inventoryRecordRepository.findByTemplate_Id(templateId);

        List<TemplateField> fields =
                templateFieldRepository.findByTemplate_IdOrderByDisplayOrderAsc(templateId);

        List<Map<String, String>> result = new ArrayList<>();

        for (InventoryRecord record : records) {

            List<InventoryValue> values =
                    inventoryValueRepository.findByInventoryRecord_Id(record.getId());

            Map<Long, String> valueMap = new HashMap<>();
            for (InventoryValue val : values) {
                valueMap.put(val.getFieldId(), val.getValue());
            }

            Map<String, String> row = new LinkedHashMap<>();

            row.put("recordId", String.valueOf(record.getId()));
            row.put("unitName", record.getUnitName());

            for (TemplateField field : fields) {
                row.put(field.getFieldName(),
                        valueMap.getOrDefault(field.getId(), ""));
            }

            result.add(row);
        }

        return result;
    }

    @Transactional
    public ResponseEntity<?> updateInventory(InventoryUpdateRecordDTO req) {

        InventoryRecord record = inventoryRecordRepository
                .findById(req.getRecordId())
                .orElseThrow(() -> new RuntimeException("Record not found"));

        if (!record.getUnitName().equals(req.getUnitName())) {
            return ResponseEntity.status(403)
                    .body("You can only update your unit data");
        }

        Template template = templateRepository.findById(req.getTemplateId())
                .orElseThrow(() -> new RuntimeException("Template not found"));

        List<InventoryValue> values =
                inventoryValueRepository.findByInventoryRecord_Id(req.getRecordId());

        if (values.isEmpty()) {
            return ResponseEntity.badRequest().body("No inventory found");
        }

        Map<Long, String> fieldMap =
                templateFieldRepository
                        .findByTemplate_IdOrderByDisplayOrderAsc(req.getTemplateId())
                        .stream()
                        .collect(Collectors.toMap(
                                TemplateField::getId,
                                f -> f.getFieldName().toLowerCase()
                        ));

        InventoryValue inwardField = null;
        InventoryValue outwardField = null;
        InventoryValue stockField = null;

        for (InventoryValue v : values) {
            String fieldName = fieldMap.get(v.getFieldId());
            if (fieldName == null) continue;

            if (fieldName.contains("inward")) inwardField = v;
            else if (fieldName.contains("outward")) outwardField = v;
            else if (fieldName.contains("stock")) stockField = v;
        }

        int inward = safeParse(inwardField.getValue());
        int outward = safeParse(outwardField.getValue());

        int previousStock = inward - outward;
        int qty = req.getChangeQty();
        ActionType action = req.getAction();

        switch (action) {
            case INWARD:
                inward += qty;
                inwardField.setValue(String.valueOf(inward));
                break;

            case OUTWARD:
                if ((inward - outward) < qty) {
                    return ResponseEntity.badRequest().body("Not enough stock");
                }
                outward += qty;
                outwardField.setValue(String.valueOf(outward));
                break;
        }

        int newStock = inward - outward;
        stockField.setValue(String.valueOf(newStock));

        inventoryValueRepository.saveAll(
                List.of(inwardField, outwardField, stockField)
        );


        InventoryLog inventoryLog = new InventoryLog(
                req.getTemplateId(),
                req.getUnitName(),
                action,
                qty,
                previousStock,
                newStock,
                req.getEId(),
                template.getTemplateName() != null ? template.getTemplateName() : "-"
        );

        publisher.publishEvent(new InventoryAuditEvent(inventoryLog));

        return ResponseEntity.ok("Stock updated successfully");
    }

//    public List<InventoryLog> getEmployeeLogs(Long eId) {
//        return inventoryLogRepository.findByPerformedByOrderByCreatedAtDesc(eId);
//    }

    public Page<InventoryLog> getLogsForAdminFiltered(
            String unitName,
            String templateName,
            ActionType action,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // ✅ All filters applied
        if (unitName != null && templateName != null && action != null) {
            return inventoryLogRepository
                    .findByUnitNameAndTemplateNameContainingIgnoreCaseAndAction(
                            unitName, templateName, action, pageable);
        }

        // ✅ Two filters
        if (unitName != null && templateName != null) {
            return inventoryLogRepository
                    .findByUnitNameAndTemplateNameContainingIgnoreCase(unitName, templateName, pageable);
        }

        if (unitName != null && action != null) {
            return inventoryLogRepository
                    .findByUnitNameAndAction(unitName, action, pageable);
        }

        if (templateName != null && action != null) {
            return inventoryLogRepository
                    .findByTemplateNameContainingIgnoreCaseAndAction(templateName, action, pageable);
        }

        // ✅ Single filters
        if (unitName != null) {
            return inventoryLogRepository.findByUnitName(unitName, pageable);
        }

        if (templateName != null) {
            return inventoryLogRepository.findByTemplateNameContainingIgnoreCase(templateName, pageable);
        }

        if (action != null) {
            return inventoryLogRepository.findByAction(action, pageable);
        }

        // ✅ No filters → return all
        return inventoryLogRepository.findAll(pageable);
    }

    public List<String> getAllUnits() {
        return inventoryLogRepository.findAllUnits();
    }


    // ================= EMPLOYEE FILTER =================
    // Commented till adminLogs are completed
//    public List<InventoryLog> getEmployeeLogsFiltered(
//            Long eId,
//            String templateName,
//            ActionType action
//    ) {
//        List<InventoryLog> logs =
//                inventoryLogRepository.findByPerformedByOrderByCreatedAtDesc(eId);
//
//        return logs.stream()
//                .filter(log ->
//                        (templateName == null || templateName.isBlank()
//                                || log.getTemplateName().toLowerCase().contains(templateName.toLowerCase()))
//                )
//                .filter(log ->
//                        (action == null || log.getAction() == action)
//                )
//                .collect(Collectors.toList());
//    }


    // ================= ADMIN FILTER =================
    public List<InventoryLog> getLogsForAdminFiltered(
            String unitName,
            String templateName,
            ActionType action
    ) {
        List<InventoryLog> logs = inventoryLogRepository.findAll();

        return logs.stream()
                .filter(log ->
                        (unitName == null || unitName.isBlank()
                                || log.getUnitName().toLowerCase().contains(unitName.toLowerCase()))
                )
                .filter(log ->
                        (templateName == null || templateName.isBlank()
                                || log.getTemplateName().toLowerCase().contains(templateName.toLowerCase()))
                )
                .filter(log ->
                        (action == null || log.getAction() == action)
                )
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }
}