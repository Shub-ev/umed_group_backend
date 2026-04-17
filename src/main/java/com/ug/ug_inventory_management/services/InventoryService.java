package com.ug.ug_inventory_management.services;
import com.ug.ug_inventory_management.common.events.InventoryAuditEvent;
import com.ug.ug_inventory_management.models.InventoryLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private  final InventoryLogRepository inventoryLogRepository;

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    public InventoryService(InventoryRecordRepository inventoryRecordRepository,
                            InventoryValueRepository inventoryValueRepository,
                            TemplateFieldRepository templateFieldRepository,
                            TemplateRepository templateRepository,ApplicationEventPublisher publisher,InventoryLogRepository inventoryLogRepository) {
        this.inventoryRecordRepository = inventoryRecordRepository;
        this.inventoryValueRepository = inventoryValueRepository;
        this.templateFieldRepository = templateFieldRepository;
        this.templateRepository = templateRepository;
        this.publisher = publisher;
        this.inventoryLogRepository=inventoryLogRepository;
    }


    @Transactional
    public void addInventory(@NotNull CreateInventoryRecordDTO request) {
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

            // VALIDATION
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
    public ResponseEntity<?> updateInventory(InventoryUpdateRecordDTO req) {

        //  Fetch record
        InventoryRecord record = inventoryRecordRepository
                .findById(req.getRecordId())
                .orElseThrow(() -> new RuntimeException("Record not found"));

        //  SECURITY CHECK (use DB value, not trusting frontend blindly)
        if (!record.getUnitName().equals(req.getUnitName())) {
            return ResponseEntity.status(403)
                    .body("You can only update your unit data");
        }

        log.info("Update Inventory: {}", req.getTemplateId());

        List<InventoryValue> values =
                inventoryValueRepository.findByInventoryRecord_Id(req.getRecordId());

        if (values.isEmpty()) {
            return ResponseEntity.badRequest().body("No inventory found for this record");
        }

        log.info("Values: {}", values);

        // Fetch all fields in ONE query (performance fix)
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

        // Safe parsing
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

        //  renamed variable to avoid conflict with Logger
        InventoryLog inventoryLog = new InventoryLog(
                req.getTemplateId(),
                req.getUnitName(),
                action.name(),
                qty,
                previousStock,
                newStock,
                req.geteId()
        );

        log.info("Publishing inventory audit event");
        publisher.publishEvent(new InventoryAuditEvent(inventoryLog));

        return ResponseEntity.ok("Stock updated successfully");
    }



    public List<InventoryLog> getEmployeeLogs(Long eId) {
        return inventoryLogRepository.findByPerformedByOrderByCreatedAtDesc(eId);
    }

    public List<InventoryLog> getLogsForAdmin(String unitName, Long templateId) {
        log.info("Admin Logs request:\nUnit Name : {}\nTemplate Id : {}", unitName, templateId);
        if((unitName == null || unitName.trim().equals("")) && (templateId == null || templateId == 0)) {
            return inventoryLogRepository.findAll();
        }
        if(templateId == null){
            return inventoryLogRepository.findByUnitNameOrderByCreatedAtDesc(unitName);
        } else {
            return inventoryLogRepository.findByUnitNameAndTemplateIdOrderByCreatedAtDesc(unitName, templateId);
        }
    }

    public List<String> getAllUnits() {
        return inventoryLogRepository.findAllUnits();
    }


}



