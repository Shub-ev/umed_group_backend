//package com.ug.ug_inventory_management.services;

package online.umedgroup.ug_inventory_management.services;

import online.umedgroup.ug_inventory_management.common.events.InventoryAuditEvent;
import online.umedgroup.ug_inventory_management.common.exceptions.IllegalArgumentException;
import online.umedgroup.ug_inventory_management.models.InventoryLog;
import online.umedgroup.ug_inventory_management.repositories.*;
import online.umedgroup.ug_inventory_management.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import online.umedgroup.ug_inventory_management.common.dtos.Record.CreateInventoryRecordDTO;
import online.umedgroup.ug_inventory_management.models.InventoryRecord;
import online.umedgroup.ug_inventory_management.models.InventoryValue;
import online.umedgroup.ug_inventory_management.models.Template;
import online.umedgroup.ug_inventory_management.models.TemplateField;
import online.umedgroup.ug_inventory_management.common.dtos.Record.UpdateInventoryRecordDTO;
import online.umedgroup.ug_inventory_management.enums.ActionType;
import jakarta.validation.constraints.NotNull;
import online.umedgroup.ug_inventory_management.common.dtos.StockAlertDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;

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
    private final UnitNameRepository unitNameRepository;

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    public InventoryService(InventoryRecordRepository inventoryRecordRepository,
                            InventoryValueRepository inventoryValueRepository,
                            TemplateFieldRepository templateFieldRepository,
                            TemplateRepository templateRepository,
                            ApplicationEventPublisher publisher,
                            InventoryLogRepository inventoryLogRepository,
                            UnitNameRepository unitNameRepository) {
        this.inventoryRecordRepository = inventoryRecordRepository;
        this.inventoryValueRepository = inventoryValueRepository;
        this.templateFieldRepository = templateFieldRepository;
        this.templateRepository = templateRepository;
        this.publisher = publisher;
        this.inventoryLogRepository = inventoryLogRepository;
        this.unitNameRepository = unitNameRepository;
    }

    @Transactional
    public void addInventory(@NotNull CreateInventoryRecordDTO request) {

        // validate template ID
        Template template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Template not found"
                ));


        String mainFieldName = template.getMainField();

        log.info("Template main field: {}", mainFieldName);

        // Check if unit name exists
        if (!unitNameRepository.existsByUnitName(request.getUnitName().trim())) {
            throw new IllegalArgumentException("Unit name does not exist");
        }

        // compare hash of this record with other records for duplication
        String rawString = request.getTemplateId().toString() +
                request.getUnitName().trim() +
                new TreeMap<>(request.getValues()).toString();

        String hash = DigestUtils.md5DigestAsHex(rawString.getBytes());

        if (inventoryRecordRepository.existsByRecordHash(hash)) {
            throw new IllegalArgumentException("Same record already exist");
        }

        // create new Record
        InventoryRecord inventoryRecord =
                new InventoryRecord(template, request.getUnitName(), hash);
        inventoryRecordRepository.save(inventoryRecord);


        List<TemplateField> fields =
                templateFieldRepository.findByTemplate_IdOrderByDisplayOrderAsc(request.getTemplateId());

        Map<String, String> requestValues =
                request.getValues() != null ? request.getValues() : new HashMap<>();

        // ✅ Get main field VALUE from request
        String mainFieldValue = "-";
        if (mainFieldName != null && requestValues.containsKey(mainFieldName)) {
            mainFieldValue = requestValues.get(mainFieldName);
        }

        log.info("Main field value for this record: {}", mainFieldValue);

        InventoryValue inwardField = null;
        InventoryValue outwardField = null;
        InventoryValue stockField = null;
        List<InventoryValue> savedValues = new ArrayList<>();

        for (TemplateField field : fields) {
            String value = requestValues.get(field.getFieldName());
            String fieldNameLower = field.getFieldName().toLowerCase();

            if (fieldNameLower.equals("inward") ||
                    fieldNameLower.equals("outward") ||
                    fieldNameLower.equals("stock")) {
                value = "0";
            } else {
                if (value == null)
                    value = "";
            }

            InventoryValue inventoryValue =
                    new InventoryValue(inventoryRecord, field.getId(), value);

            savedValues.add(inventoryValue);

            if (fieldNameLower.contains("inward")) inwardField = inventoryValue;
            else if (fieldNameLower.contains("outward")) outwardField = inventoryValue;
            else if (fieldNameLower.contains("stock")) stockField = inventoryValue;
        }

        inventoryValueRepository.saveAll(savedValues);

        InventoryLog inventoryLog = new InventoryLog(
                request.getTemplateId(),
                request.getUnitName(),
                ActionType.CREATE,
                0,
                0,
                0,
                request.getEId(),
                template.getTemplateName() != null ? template.getTemplateName() : "-"
        );

        log.info("Publishing inventory audit event for new inventory");
        publisher.publishEvent(new InventoryAuditEvent(inventoryLog));
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



    @Transactional
    public ResponseEntity<?> deleteInventory(Long recordId, Long eId, String unitName) {

        InventoryRecord record = inventoryRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        // employee can delete only his own record
        if (!record.getUnitName().equals(unitName)) {
            return ResponseEntity.status(403)
                    .body("You can only delete your own record");
        }

        // delete child values first
        inventoryValueRepository.deleteByInventoryRecord_Id(recordId);

        // create delete log
        InventoryLog inventoryLog = new InventoryLog(
                record.getTemplate().getId(),
                unitName,
                ActionType.DELETE,
                0,
                0,
                0,
                eId,
                record.getTemplate().getTemplateName()
        );
        // delete main record
        inventoryRecordRepository.delete(record);

        log.info("Publishing inventory audit event for deleted inventory");
        publisher.publishEvent(new InventoryAuditEvent(inventoryLog));

        return ResponseEntity.ok("Record deleted successfully");
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

    public List<Map<String, String>> searchFromInventory(Long templateId, String field) {

        if (templateId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Template id is required");
        }

        // 1. Extract template and its main field
        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No template found with id " + templateId
                ));
        String mainFieldName = template.getMainField();

        // 2. Find mainField Id
        TemplateField mainFieldId = templateFieldRepository.findByTemplate_IdAndFieldNameIgnoreCase(templateId, mainFieldName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Main field not found"
                ));

        // 3. Get Inventory values with same field id and value(field)
        List<InventoryValue> matchedValues = inventoryValueRepository.findByFieldIdAndValueIgnoreCase(
                mainFieldId.getId(),
                field.trim()
        );

        // 4. Get matched record IDs with above inventory values
        Set<Long> recordIds = matchedValues.stream()
                .map(v -> v.getInventoryRecord().getId())
                .collect(Collectors.toSet());

        // 5. Fetch all records by ids
        List<InventoryRecord> records = inventoryRecordRepository.findAllById(recordIds);

        // 6. Fetch all template fields
        List<TemplateField> fields =
                templateFieldRepository.findByTemplate_IdOrderByDisplayOrderAsc(templateId);

        // 7. Build Response
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

            for (TemplateField f : fields) {
                row.put(f.getFieldName(),
                        valueMap.getOrDefault(f.getId(), ""));
            }

            result.add(row);
        }

        return result;
    }

    @Transactional
    public ResponseEntity<?> updateInventory(UpdateInventoryRecordDTO req) {

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

//    public Page<InventoryLog> getLogsForAdminFiltered(
//            String unitName,
//            String templateName,
//            ActionType action,
//            int page,
//            int size
//    ) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//
//        // ✅ All filters applied
//        if (unitName != null && templateName != null && action != null) {
//            return inventoryLogRepository
//                    .findByUnitNameAndTemplateNameContainingIgnoreCaseAndAction(
//                            unitName, templateName, action, pageable);
//        }
//
//        // ✅ Two filters
//        if (unitName != null && templateName != null) {
//            return inventoryLogRepository
//                    .findByUnitNameAndTemplateNameContainingIgnoreCase(unitName, templateName, pageable);
//        }
//
//        if (unitName != null && action != null) {
//            return inventoryLogRepository
//                    .findByUnitNameAndAction(unitName, action, pageable);
//        }
//
//        if (templateName != null && action != null) {
//            return inventoryLogRepository
//                    .findByTemplateNameContainingIgnoreCaseAndAction(templateName, action, pageable);
//        }
//
//        // ✅ Single filters
//        if (unitName != null) {
//            return inventoryLogRepository.findByUnitName(unitName, pageable);
//        }
//
//        if (templateName != null) {
//            return inventoryLogRepository.findByTemplateNameContainingIgnoreCase(templateName, pageable);
//        }
//
//        if (action != null) {
//            return inventoryLogRepository.findByAction(action, pageable);
//        }
//
//        // ✅ No filters → return all
//        return inventoryLogRepository.findAll(pageable);
//    }

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

    public Page<InventoryLog> getEmployeeLogsFiltered(
            Long eId,
            String templateName,
            ActionType action,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return inventoryLogRepository.findEmployeeLogs(
                eId,
                templateName,
                action,
                pageable
        );
    }

    public Page<InventoryLog> getLogsForAdminFiltered(
            String unitName,
            String templateName,
            ActionType action,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return inventoryLogRepository.findFilteredLogs(
                unitName,
                templateName,
                action,
                pageable
        );
    }


    public List<StockAlertDTO> getAllLowStockAlerts() {
        List<StockAlertDTO> alerts = new ArrayList<>();

        List<InventoryRecord> records = inventoryRecordRepository.findAll();

        for (InventoryRecord record : records) {
            if (record.getTemplate() == null) {
                continue;
            }

            Long templateId = record.getTemplate().getId();

            List<TemplateField> fields =
                    templateFieldRepository.findByTemplate_IdOrderByDisplayOrderAsc(templateId);

            List<InventoryValue> values =
                    inventoryValueRepository.findByInventoryRecord_Id(record.getId());

            Map<Long, String> valueMap = new HashMap<>();
            for (InventoryValue value : values) {
                valueMap.put(value.getFieldId(), value.getValue());
            }

            Integer stock = null;

            for (TemplateField field : fields) {
                String fieldName = field.getFieldName();
                if (fieldName != null && fieldName.trim().toLowerCase().contains("stock")) {
                    stock = safeParse(valueMap.get(field.getId()));
                    break;
                }
            }

            if (stock != null && stock < 10) {
                alerts.add(new StockAlertDTO(
                        record.getUnitName(),
                        record.getTemplate().getTemplateName() != null
                                ? record.getTemplate().getTemplateName()
                                : "-",
                        stock
                ));
            }
        }

        return alerts;
    }

    public List<StockAlertDTO> getLowStockAlertsForUnit(String unitName) {
        if (unitName == null || unitName.trim().isEmpty()) {
            return List.of();
        }

        return getAllLowStockAlerts().stream()
                .filter(alert -> unitName.equals(alert.getUnitName()))
                .toList();
    }
}