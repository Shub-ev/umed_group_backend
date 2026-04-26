package online.umedgroup.ug_inventory_management.services;

import online.umedgroup.ug_inventory_management.common.dtos.Record.CreateInventoryRecordDTO;
import online.umedgroup.ug_inventory_management.common.dtos.Record.UpdateInventoryRecordDTO;
import online.umedgroup.ug_inventory_management.common.dtos.StockAlertDTO;
import online.umedgroup.ug_inventory_management.common.events.InventoryAuditEvent;
import online.umedgroup.ug_inventory_management.common.exceptions.IllegalArgumentException;
import online.umedgroup.ug_inventory_management.enums.ActionType;
import online.umedgroup.ug_inventory_management.models.InventoryLog;
import online.umedgroup.ug_inventory_management.models.InventoryRecord;
import online.umedgroup.ug_inventory_management.models.InventoryValue;
import online.umedgroup.ug_inventory_management.models.Template;
import online.umedgroup.ug_inventory_management.models.TemplateField;
import online.umedgroup.ug_inventory_management.repositories.InventoryLogRepository;
import online.umedgroup.ug_inventory_management.repositories.InventoryRecordRepository;
import online.umedgroup.ug_inventory_management.repositories.InventoryValueRepository;
import online.umedgroup.ug_inventory_management.repositories.TemplateFieldRepository;
import online.umedgroup.ug_inventory_management.repositories.TemplateRepository;
import online.umedgroup.ug_inventory_management.repositories.UnitNameRepository;
import online.umedgroup.ug_inventory_management.repositories.StockAlertRepository;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
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
    private final StockAlertRepository stockAlertRepository;


    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    public InventoryService(InventoryRecordRepository inventoryRecordRepository,
                            InventoryValueRepository inventoryValueRepository,
                            TemplateFieldRepository templateFieldRepository,
                            TemplateRepository templateRepository,
                            ApplicationEventPublisher publisher,
                            InventoryLogRepository inventoryLogRepository,
                            UnitNameRepository unitNameRepository,StockAlertRepository stockAlertRepository) {
        this.inventoryRecordRepository = inventoryRecordRepository;
        this.inventoryValueRepository = inventoryValueRepository;
        this.templateFieldRepository = templateFieldRepository;
        this.templateRepository = templateRepository;
        this.publisher = publisher;
        this.inventoryLogRepository = inventoryLogRepository;
        this.unitNameRepository = unitNameRepository;
        this.stockAlertRepository = stockAlertRepository;
    }

    @Transactional
    public void addInventory(@NotNull CreateInventoryRecordDTO request) {

        Template template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Template not found"
                ));

        String mainFieldName = template.getMainField();
        log.info("Template main field: {}", mainFieldName);

        String unitName = request.getUnitName() == null ? "" : request.getUnitName().trim();
        if (unitName.isEmpty()) {
            throw new IllegalArgumentException("Unit name is required");
        }

        if (!unitNameRepository.existsByUnitName(unitName)) {
            throw new IllegalArgumentException("Unit name does not exist");
        }

        Map<String, String> requestValues = normalizeValueMap(request.getValues());

        String rawString = request.getTemplateId().toString()
                + unitName
                + new TreeMap<>(requestValues).toString();

        String hash = DigestUtils.md5DigestAsHex(rawString.getBytes());

        if (inventoryRecordRepository.existsByRecordHash(hash)) {
            throw new IllegalArgumentException("Same record already exist");
        }

        InventoryRecord inventoryRecord = new InventoryRecord(template, unitName, hash);
        inventoryRecordRepository.save(inventoryRecord);

        List<TemplateField> fields =
                templateFieldRepository.findByTemplate_IdOrderByDisplayOrderAsc(request.getTemplateId());

        String mainFieldValue = resolveMainFieldValue(mainFieldName, requestValues);
        log.info("Main field value for this record: {}", mainFieldValue);

        InventoryValue inwardField = null;
        InventoryValue outwardField = null;
        InventoryValue stockField = null;

        List<InventoryValue> savedValues = new ArrayList<>();

        for (TemplateField field : fields) {
            String fieldName = field.getFieldName();
            String fieldKey = normalizeKey(fieldName);

            String value = requestValues.get(fieldKey);

            if ("inward".equals(fieldKey) ||
                    "outward".equals(fieldKey) ||
                    "stock".equals(fieldKey)) {
                value = "0";
            } else if (value == null) {
                value = "";
            }

            InventoryValue inventoryValue =
                    new InventoryValue(inventoryRecord, field.getId(), value);

            savedValues.add(inventoryValue);

            if (fieldKey != null && fieldKey.contains("inward")) inwardField = inventoryValue;
            else if (fieldKey != null && fieldKey.contains("outward")) outwardField = inventoryValue;
            else if (fieldKey != null && fieldKey.contains("stock")) stockField = inventoryValue;
        }

        inventoryValueRepository.saveAll(savedValues);
        Long recordId = inventoryRecord.getId();

        InventoryLog inventoryLog = new InventoryLog(
                request.getTemplateId(),
                unitName,
                ActionType.CREATE,
                0,
                0,
                0,
                request.getEId(),
                template.getTemplateName() != null ? template.getTemplateName() : "-",
                mainFieldValue,
                recordId
        );
//        inventoryLog.setMainFieldValue(mainFieldValue);

        log.info("Publishing inventory audit event for new inventory");
        publisher.publishEvent(new InventoryAuditEvent(inventoryLog));
    }

    @Transactional
    public ResponseEntity<?> deleteInventory(Long recordId, Long eId, String unitName) {

        InventoryRecord record = inventoryRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        String cleanedUnitName = unitName == null ? "" : unitName.trim();
        if (!Objects.equals(record.getUnitName(), cleanedUnitName)) {
            return ResponseEntity.status(403)
                    .body("You can only delete your own record");
        }

        Template template = record.getTemplate();
        if (template == null) {
            return ResponseEntity.status(404).body("Template not found for record");
        }

        String mainFieldName = template.getMainField();

        List<TemplateField> fields =
                templateFieldRepository.findByTemplate_IdOrderByDisplayOrderAsc(template.getId());

        List<InventoryValue> values =
                inventoryValueRepository.findByInventoryRecord_Id(recordId);

        Map<Long, String> valueByFieldId = buildValueByFieldIdMap(values);
        Map<String, String> fieldNameValueMap = buildFieldNameValueMap(fields, valueByFieldId);

        String mainFieldValue = resolveMainFieldValue(mainFieldName, fieldNameValueMap);
        log.info("Delete log main field value: {}", mainFieldValue);

        inventoryValueRepository.deleteByInventoryRecord_Id(recordId);
        inventoryRecordRepository.delete(record);

        InventoryLog inventoryLog = new InventoryLog(
                template.getId(),
                cleanedUnitName,
                ActionType.DELETE,
                0,
                0,
                0,
                eId,
                template.getTemplateName() != null ? template.getTemplateName() : "-",
                mainFieldValue,
                recordId
        );
//        inventoryLog.setMainFieldValue(mainFieldValue);

        log.info("Publishing inventory audit event for deleted inventory");
        publisher.publishEvent(new InventoryAuditEvent(inventoryLog));

        return ResponseEntity.ok("Record deleted successfully");
    }

    public List<Map<String, String>> getInventorySummary(Long templateId) {

        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No template found with id " + templateId
                ));

        List<InventoryRecord> records =
                inventoryRecordRepository.findByTemplate_Id(templateId);

        List<TemplateField> fields =
                templateFieldRepository.findByTemplate_IdOrderByDisplayOrderAsc(templateId);

        String mainFieldName = template.getMainField();

        List<Map<String, String>> result = new ArrayList<>();

        for (InventoryRecord record : records) {

            List<InventoryValue> values =
                    inventoryValueRepository.findByInventoryRecord_Id(record.getId());

            Map<Long, String> valueByFieldId = buildValueByFieldIdMap(values);
            Map<String, String> fieldNameValueMap = buildFieldNameValueMap(fields, valueByFieldId);

            Map<String, String> row = new LinkedHashMap<>();
            row.put("recordId", String.valueOf(record.getId()));
            row.put("unitName", record.getUnitName());
            row.put("mainFieldName", mainFieldName != null ? mainFieldName : "-");
            row.put("mainFieldValue", resolveMainFieldValue(mainFieldName, fieldNameValueMap));

            for (TemplateField field : fields) {
                String fieldName = field.getFieldName();
                row.put(fieldName, valueByFieldId.getOrDefault(field.getId(), ""));
            }

            result.add(row);
        }

        return result;
    }

    public List<Map<String, String>> searchFromInventory(Long templateId, String field) {

        if (templateId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Template id is required");
        }

        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No template found with id " + templateId
                ));

        String mainFieldName = template.getMainField();
        if (mainFieldName == null || mainFieldName.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Main field not configured for this template"
            );
        }

        List<TemplateField> fields =
                templateFieldRepository.findByTemplate_IdOrderByDisplayOrderAsc(templateId);

        TemplateField mainField = fields.stream()
                .filter(f -> f.getFieldName() != null
                        && f.getFieldName().equalsIgnoreCase(mainFieldName.trim()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Main field not found"
                ));

        List<InventoryValue> matchedValues = inventoryValueRepository.findByFieldIdAndValueIgnoreCase(
                mainField.getId(),
                field.trim()
        );

        Set<Long> recordIds = matchedValues.stream()
                .map(v -> v.getInventoryRecord().getId())
                .collect(Collectors.toSet());

        if (recordIds.isEmpty()) {
            return List.of();
        }

        List<InventoryRecord> records = inventoryRecordRepository.findAllById(recordIds);

        List<Map<String, String>> result = new ArrayList<>();

        for (InventoryRecord record : records) {

            List<InventoryValue> values =
                    inventoryValueRepository.findByInventoryRecord_Id(record.getId());

            Map<Long, String> valueByFieldId = buildValueByFieldIdMap(values);
            Map<String, String> fieldNameValueMap = buildFieldNameValueMap(fields, valueByFieldId);

            Map<String, String> row = new LinkedHashMap<>();
            row.put("recordId", String.valueOf(record.getId()));
            row.put("unitName", record.getUnitName());
            row.put("mainFieldName", mainFieldName);
            row.put("mainFieldValue", resolveMainFieldValue(mainFieldName, fieldNameValueMap));

            for (TemplateField f : fields) {
                String fieldName = f.getFieldName();
                row.put(fieldName, valueByFieldId.getOrDefault(f.getId(), ""));
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

        String cleanedUnitName = req.getUnitName() == null ? "" : req.getUnitName().trim();
        if (!Objects.equals(record.getUnitName(), cleanedUnitName)) {
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

        List<TemplateField> fields =
                templateFieldRepository.findByTemplate_IdOrderByDisplayOrderAsc(req.getTemplateId());

        Map<Long, String> fieldIdToLowerName = buildFieldIdToLowerNameMap(fields);

        Map<String, String> fieldNameValueMap = new HashMap<>();
        for (InventoryValue v : values) {
            String fieldName = fieldIdToLowerName.get(v.getFieldId());
            if (fieldName != null) {
                fieldNameValueMap.put(fieldName, v.getValue());
            }
        }

        String mainFieldName = template.getMainField();
        String mainFieldValue = resolveMainFieldValue(mainFieldName, fieldNameValueMap);

        InventoryValue inwardField = null;
        InventoryValue outwardField = null;
        InventoryValue stockField = null;

        for (InventoryValue v : values) {
            String fieldName = fieldIdToLowerName.get(v.getFieldId());
            if (fieldName == null) continue;

            if (fieldName.contains("inward")) inwardField = v;
            else if (fieldName.contains("outward")) outwardField = v;
            else if (fieldName.contains("stock")) stockField = v;
        }

        if (inwardField == null || outwardField == null || stockField == null) {
            return ResponseEntity.badRequest().body("Required stock fields not found");
        }

        if (req.getAction() == null) {
            return ResponseEntity.badRequest().body("Action is required");
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

            default:
                return ResponseEntity.badRequest().body("Invalid action");
        }

        int newStock = inward - outward;
        stockField.setValue(String.valueOf(newStock));

        inventoryValueRepository.saveAll(List.of(inwardField, outwardField, stockField));
        Long recordId = req.getRecordId();

        InventoryLog inventoryLog = new InventoryLog(
                req.getTemplateId(),
                cleanedUnitName,
                action,
                qty,
                previousStock,
                newStock,
                req.getEId(),
                template.getTemplateName() != null ? template.getTemplateName() : "-",
                mainFieldValue,
                recordId
        );
//        inventoryLog.setMainFieldValue(mainFieldValue);

        publisher.publishEvent(new InventoryAuditEvent(inventoryLog));

        return ResponseEntity.ok("Stock updated successfully");
    }

    public List<String> getAllUnits() {
        return inventoryLogRepository.findAllUnits();
    }

    public Page<InventoryLog> getEmployeeLogsFiltered(
            Long eId,
            String mainFieldValue,
            ActionType action,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return inventoryLogRepository.findEmployeeLogs(
                eId,
                mainFieldValue,
                action,
                pageable
        );
    }

    public Page<InventoryLog> getLogsForAdminFiltered(
            String unitName,
            String mainFieldValue,
            ActionType action,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return inventoryLogRepository.findFilteredLogs(
                unitName,
                mainFieldValue,
                action,
                pageable
        );
    }

    //main field ba
//    public List<StockAlertDTO> getAllLowStockAlerts() {
//
//        List<InventoryRecord> records = inventoryRecordRepository.findAll();
//
//        Map<Long, List<TemplateField>> fieldsCache = new HashMap<>();
//        Map<String, Integer> stockByUnitAndMainField = new HashMap<>();
//        Map<String, StockAlertDTO> alertInfoByKey = new HashMap<>();
//
//        for (InventoryRecord record : records) {
//
//            if (record.getTemplate() == null) continue;
//
//            String unitName = record.getUnitName();
//            if (unitName == null || unitName.trim().isEmpty()) continue;
//
//            Long templateId = record.getTemplate().getId();
//
//            List<TemplateField> fields = fieldsCache.computeIfAbsent(
//                    templateId,
//                    id -> templateFieldRepository.findByTemplate_IdOrderByDisplayOrderAsc(id)
//            );
//
//            List<InventoryValue> values =
//                    inventoryValueRepository.findByInventoryRecord_Id(record.getId());
//
//            Map<Long, String> valueByFieldId = buildValueByFieldIdMap(values);
//
//            String mainFieldName = record.getTemplate().getMainField();
//
//            String mainFieldValue = null;
//            Integer stock = null;
//
//
//            for (TemplateField field : fields) {
//
//                String fieldName = field.getFieldName();
//                if (fieldName == null) continue;
//
//                //  mainField extraction
//                if (mainFieldValue == null &&
//                        fieldName.equalsIgnoreCase(mainFieldName)) {
//
//                    mainFieldValue = valueByFieldId.get(field.getId());
//                }
//
//                //  stock extraction
//                if (stock == null &&
//                        fieldName.trim().toLowerCase().contains("stock")) {
//
//                    stock = safeParse(valueByFieldId.get(field.getId()));
//                }
//
//                // if both found
//                if (mainFieldValue != null && stock != null) {
//                    break;
//                }
//            }
//
//            if (mainFieldValue == null || mainFieldValue.trim().isEmpty()) continue;
//            if (stock == null) continue;
//
//            String key = unitName.trim().toLowerCase() + "|" + mainFieldValue.trim().toLowerCase();
//
//            stockByUnitAndMainField.merge(key, stock, Integer::sum);
//
//            alertInfoByKey.putIfAbsent(
//                    key,
//                    new StockAlertDTO(
//                            unitName,
//                            mainFieldValue,
//                            record.getTemplate().getTemplateName() != null
//                                    ? record.getTemplate().getTemplateName()
//                                    : "-",
//                            0
//                    )
//            );
//        }
//
//        List<StockAlertDTO> alerts = new ArrayList<>();
//
//        for (Map.Entry<String, Integer> entry : stockByUnitAndMainField.entrySet()) {
//
//            Integer totalStock = entry.getValue();
//
//            if (totalStock < 10) {
//                StockAlertDTO dto = alertInfoByKey.get(entry.getKey());
//                if (dto != null) {
//                    dto.setStock(totalStock);
//                    alerts.add(dto);
//                }
//            }
//        }
//
//        return alerts;
//    }

    public List<StockAlertDTO> getAllLowStockAlerts() {

        List<Object[]> results = stockAlertRepository
                .findLowStockGrouped("stock", 10);

        List<StockAlertDTO> alerts = new ArrayList<>();

        for (Object[] row : results) {

            String unitName = (String) row[0];
            String mainFieldValue = (String) row[1];
            Integer stock = ((Number) row[2]).intValue();

            alerts.add(new StockAlertDTO(
                    unitName,
                    mainFieldValue,
                    "-", // optional templateName
                    stock
            ));
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

    private Map<String, String> normalizeValueMap(Map<String, String> source) {
        Map<String, String> normalized = new HashMap<>();
        if (source == null) {
            return normalized;
        }

        for (Map.Entry<String, String> entry : source.entrySet()) {
            String key = normalizeKey(entry.getKey());
            if (key != null && !key.isEmpty()) {
                normalized.put(key, entry.getValue());
            }
        }
        return normalized;
    }

    private Map<Long, String> buildValueByFieldIdMap(List<InventoryValue> values) {
        Map<Long, String> valueByFieldId = new HashMap<>();
        for (InventoryValue value : values) {
            valueByFieldId.put(value.getFieldId(), value.getValue());
        }
        return valueByFieldId;
    }

    private Map<String, String> buildFieldNameValueMap(List<TemplateField> fields, Map<Long, String> valueByFieldId) {
        Map<String, String> fieldNameValueMap = new HashMap<>();
        for (TemplateField field : fields) {
            String fieldName = normalizeKey(field.getFieldName());
            if (fieldName != null && !fieldName.isEmpty()) {
                fieldNameValueMap.put(fieldName, valueByFieldId.getOrDefault(field.getId(), ""));
            }
        }
        return fieldNameValueMap;
    }

    private Map<Long, String> buildFieldIdToLowerNameMap(List<TemplateField> fields) {
        Map<Long, String> fieldIdToLowerName = new HashMap<>();
        for (TemplateField field : fields) {
            String fieldName = normalizeKey(field.getFieldName());
            if (fieldName != null && !fieldName.isEmpty()) {
                fieldIdToLowerName.put(field.getId(), fieldName);
            }
        }
        return fieldIdToLowerName;
    }

    private String resolveMainFieldValue(String mainFieldName, Map<String, String> fieldNameValueMap) {
        String key = normalizeKey(mainFieldName);
        if (key == null || key.isEmpty()) {
            return "-";
        }

        String value = fieldNameValueMap.get(key);
        return (value == null || value.trim().isEmpty()) ? "-" : value.trim();
    }

    private String normalizeKey(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }

    private int safeParse(String value) {
        try {
            return (value == null || value.trim().isEmpty())
                    ? 0
                    : Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}