package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.common.dtos.CreateInventoryRecordDTO;
import com.ug.ug_inventory_management.models.InventoryRecord;
import com.ug.ug_inventory_management.models.InventoryValue;
import com.ug.ug_inventory_management.models.Template;
import com.ug.ug_inventory_management.models.TemplateField;
import com.ug.ug_inventory_management.repositories.InventoryRecordRepository;
import com.ug.ug_inventory_management.repositories.InventoryValueRepository;
import com.ug.ug_inventory_management.repositories.TemplateFieldRepository;
import com.ug.ug_inventory_management.common.dtos.InventoryUpdateRequest;
import org.springframework.http.ResponseEntity;
import com.ug.ug_inventory_management.repositories.TemplateRepository;
import jakarta.transaction.Transactional;
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

    public InventoryService(InventoryRecordRepository inventoryRecordRepository,
                            InventoryValueRepository inventoryValueRepository,
                            TemplateFieldRepository templateFieldRepository,
                            TemplateRepository templateRepository) {
        this.inventoryRecordRepository = inventoryRecordRepository;
        this.inventoryValueRepository = inventoryValueRepository;
        this.templateFieldRepository = templateFieldRepository;
        this.templateRepository = templateRepository;
    }

    /*  #### Correct this comment
     * saveRecord(CreateInventoryRecordDTO):
     * Creates record and save it to database.
     *
     * @param CreateInventoryRecordDTO:
     * This DTO provides,
     * 1. templateId
     * 2. unitName
     * 3. values
     * To the service method & method saves this data into InventoryRecord and InventoryValue
     */
    @Transactional
    public void saveRecord(@NotNull CreateInventoryRecordDTO request) {
        // A. Store record
        Template template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new RuntimeException("Template not found"));
        InventoryRecord inventoryRecord = new InventoryRecord(
                    template,
                    request.getUnitName()
                );
        inventoryRecordRepository.save(inventoryRecord);

        List<TemplateField> fields =
                templateFieldRepository.findByTemplate_Id(request.getTemplateId());

        for (TemplateField field : fields) {
            String value = request.getValues().get(field.getFieldName());

            // ✅ VALIDATION (important)
            if (value == null) {
                // #### Handle exception properly
                throw new RuntimeException(
                        "Missing value for field: " + field.getFieldName()
                );
            }

            // B. Store field to InventoryValue table
            InventoryValue inventoryValue = new InventoryValue(
                    inventoryRecord,
                    field.getId(),
                    value
                );
            inventoryValueRepository.save(inventoryValue);
        }
    }

    // ✅ FETCH INVENTORY (DYNAMIC TABLE)
    public List<Map<String, String>> getInventory(@NotNull Long templateId, @NotNull String unitName) {

//        List<InventoryRecord> records =
//                inventoryRecordRepository.findByTemplateIdAndUnitName(templateId, unitName);

        List<InventoryRecord> records = inventoryRecordRepository.findByTemplate_Id(templateId);

        List<TemplateField> fields =
                templateFieldRepository.findByTemplate_Id(templateId);

        Map<Long, String> fieldMap = fields.stream()
                .collect(Collectors.toMap(
                        TemplateField::getId,
                        TemplateField::getFieldName
                ));

        List<Map<String, String>> result = new ArrayList<>();

        for (InventoryRecord record : records) {

            List<InventoryValue> values =
                    inventoryValueRepository.findByInventoryRecord_Id(record.getId());

            Map<String, String> row = new LinkedHashMap<>();

            for (TemplateField field : fields) {
                row.put(field.getFieldName(), "");
            }

            for (InventoryValue val : values) {
                String fieldName = fieldMap.get(val.getFieldId());
                if (fieldName != null) {
                    row.put(fieldName, val.getValue());
                }
            }

            result.add(row);
        }

        return result;
    }


    public List<Map<String, String>> getUnitWiseSummary(Long templateId) {

        List<InventoryRecord> records =
                inventoryRecordRepository.findByTemplate_Id(templateId);

        List<TemplateField> fields =
                templateFieldRepository.findByTemplate_Id(templateId);

        Map<Long, String> fieldMap = fields.stream()
                .collect(Collectors.toMap(
                        TemplateField::getId,
                        TemplateField::getFieldName
                ));


        Map<String, InventoryRecord> latestRecordPerUnit = new HashMap<>();

        for (InventoryRecord record : records) {
            String unitName = record.getUnitName();

            if (!latestRecordPerUnit.containsKey(unitName) ||
                    record.getId() > latestRecordPerUnit.get(unitName).getId()) {
                latestRecordPerUnit.put(unitName, record);
            }
        }

        List<Map<String, String>> result = new ArrayList<>();

        for (Map.Entry<String, InventoryRecord> entry : latestRecordPerUnit.entrySet()) {
            String unitId = entry.getKey();
            InventoryRecord record = entry.getValue();

            List<InventoryValue> values =
                    inventoryValueRepository.findByInventoryRecord_Id(record.getId());

            Map<String, String> row = new LinkedHashMap<>();
            row.put("unitId", String.valueOf(unitId));

            // initialize all fields
            for (TemplateField field : fields) {
                row.put(field.getFieldName(), "");
            }

            // fill actual values
            for (InventoryValue val : values) {
                String fieldName = fieldMap.get(val.getFieldId());
                if (fieldName != null) {
                    row.put(fieldName, val.getValue());
                }
            }

            result.add(row);
        }

        return result;
    }

    public List<Map<String, String>> getInventorySummary(Long templateId) {

        // ❗ REMOVE unitId filter
        List<InventoryRecord> records =
                inventoryRecordRepository.findByTemplate_Id(templateId);

        List<TemplateField> fields =
                templateFieldRepository.findByTemplate_Id(templateId);

        List<Map<String, String>> result = new ArrayList<>();

        for (InventoryRecord record : records) {

            List<InventoryValue> values =
                    inventoryValueRepository.findByInventoryRecord_Id(record.getId());

            // 🔥 OPTIMIZATION (important)
            Map<Long, String> valueMap = new HashMap<>();
            for (InventoryValue val : values) {
                valueMap.put(val.getFieldId(), val.getValue());
            }

            Map<String, String> row = new LinkedHashMap<>();

            // ✅ ADD UNIT INFO (VERY IMPORTANT)
            row.put("unitName", String.valueOf(record.getUnitName()));

            for (TemplateField field : fields) {
                row.put(
                        field.getFieldName(),
                        valueMap.getOrDefault(field.getId(), "")
                );
            }

            result.add(row);
        }

        return result;
    }










    public ResponseEntity<?> updateInventory(InventoryUpdateRequest req, String role) {

        if (!"EMPLOYEE".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body("Only employees can update");
        }

        // 🔥 Fetch fields dynamically
        List<InventoryValue> inwardList = valueRepo.findFieldByName(
                req.getTemplateId(), req.getUnitId(), "inward");

        List<InventoryValue> outwardList = valueRepo.findFieldByName(
                req.getTemplateId(), req.getUnitId(), "outward");

        List<InventoryValue> stockList = valueRepo.findFieldByName(
                req.getTemplateId(), req.getUnitId(), "stock");

        if (inwardList.isEmpty() || outwardList.isEmpty() || stockList.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Template must contain inward, outward and stock");
        }

        InventoryValue inwardField = inwardList.get(0);
        InventoryValue outwardField = outwardList.get(0);
        InventoryValue stockField = stockList.get(0);

        // ✅ Validate template structure
        if (inwardField == null || outwardField == null || stockField == null) {
            return ResponseEntity.badRequest()
                    .body("Template must contain inward, outward and stock");
        }

        System.out.println("INWARD FIELD: " + inwardField);
        System.out.println("OUTWARD FIELD: " + outwardField);
        System.out.println("STOCK FIELD: " + stockField);

        int inward = Integer.parseInt(
                inwardField.getValue() == null ? "0" : inwardField.getValue().trim()
        );

        int outward = Integer.parseInt(
                outwardField.getValue() == null ? "0" : outwardField.getValue().trim()
        );

        int qty = req.getChangeQty();

        // 🔥 Update inward/outward
        if ("INWARD".equalsIgnoreCase(req.getAction())) {
            inward += qty;
            inwardField.setValue(String.valueOf(inward));
        }
        else if ("OUTWARD".equalsIgnoreCase(req.getAction())) {

            if ((inward - outward) < qty) {
                return ResponseEntity.badRequest().body("Not enough stock");
            }

            outward += qty;
            outwardField.setValue(String.valueOf(outward));
        }

        // 🔥 ALWAYS calculate stock
        int stock = inward - outward;
        stockField.setValue(String.valueOf(stock));

        // 🔥 Save all
        valueRepo.save(inwardField);
        valueRepo.save(outwardField);
        valueRepo.save(stockField);

        return ResponseEntity.ok("Stock updated successfully");
    }
    }



