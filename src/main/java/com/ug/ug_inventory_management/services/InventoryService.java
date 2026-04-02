package com.ug.ug_inventory_management.services;
import com.ug.ug_inventory_management.common.events.InventoryAuditEvent;
import com.ug.ug_inventory_management.models.InventoryLog;
import org.springframework.context.ApplicationEventPublisher;
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
import com.ug.ug_inventory_management.common.dtos.InventoryUpdateRequest;
import org.springframework.http.ResponseEntity;
import com.ug.ug_inventory_management.repositories.TemplateRepository;
import com.ug.ug_inventory_management.enums.ActionType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRecordRepository inventoryRecordRepository;
    private final InventoryValueRepository inventoryValueRepository;
    private final TemplateFieldRepository templateFieldRepository;
    private final TemplateRepository templateRepository;
    private final ApplicationEventPublisher publisher;



    public InventoryService(InventoryRecordRepository inventoryRecordRepository,
                            InventoryValueRepository inventoryValueRepository,
                            TemplateFieldRepository templateFieldRepository,
                            TemplateRepository templateRepository,ApplicationEventPublisher publisher) {
        this.inventoryRecordRepository = inventoryRecordRepository;
        this.inventoryValueRepository = inventoryValueRepository;
        this.templateFieldRepository = templateFieldRepository;
        this.templateRepository = templateRepository;
        this.publisher = publisher;
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
                templateFieldRepository.findByTemplate_IdOrderByDisplayOrderAsc(request.getTemplateId());

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



//     ✅ FETCH INVENTORY (DYNAMIC TABLE)
    public List<Map<String, String>> getInventory(@NotNull Long templateId, @NotNull String unitName) {



        List<InventoryRecord> records = inventoryRecordRepository.findByTemplate_Id(templateId);

        List<TemplateField> fields =
                templateFieldRepository.findByTemplate_IdOrderByDisplayOrderAsc(templateId);

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



    private int safeParse(String value) {
        try {
            return (value == null || value.trim().isEmpty())
                    ? 0
                    : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    public List<Map<String, String>> getUnitWiseSummary(Long templateId) {

        List<InventoryRecord> records =
                inventoryRecordRepository.findByTemplate_Id(templateId);

        List<TemplateField> fields =
                templateFieldRepository.findByTemplate_IdOrderByDisplayOrderAsc(templateId);

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
                templateFieldRepository.findByTemplate_IdOrderByDisplayOrderAsc(templateId);

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


    @Transactional
    public ResponseEntity<?> updateInventory(InventoryUpdateRequest req, String role,Long eId ,String unitName) {

        if (role == null || !role.toUpperCase().contains("EMPLOYEE")) {
            return ResponseEntity.status(403).body("Only employees can update");
        }

        List<InventoryValue> values =
                inventoryValueRepository.findByInventoryRecord_Id(req.getRecordId());

        if (values.isEmpty()) {
            return ResponseEntity.badRequest().body("No inventory found for this record");
        }

        // ✅ Fetch all fields in ONE query (performance fix)
        Map<Long, String> fieldMap = templateFieldRepository
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

        if (inwardField == null || outwardField == null || stockField == null) {
            return ResponseEntity.badRequest()
                    .body("Template must contain inward, outward and stock fields");
        }

        // ✅ Safe parsing
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

        inventoryValueRepository.saveAll(
                List.of(inwardField, outwardField, stockField)
        );
        Long performedBy = eId;
        InventoryLog log = new InventoryLog(
                req.getTemplateId(),
                unitName,
                action.name(),
                qty,
                previousStock,
                newStock,
                performedBy
        );

        System.out.println("DEBUG → Before event publish");
        publisher.publishEvent(new InventoryAuditEvent(log));
        System.out.println("DEBUG → Before event publish");

        return ResponseEntity.ok("Stock updated successfully");
    }


}



