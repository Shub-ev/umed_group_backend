

package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.common.dtos.InventoryRequest;
import com.ug.ug_inventory_management.models.InventoryRecord;
import com.ug.ug_inventory_management.models.InventoryValue;
import com.ug.ug_inventory_management.models.TemplateField;
import com.ug.ug_inventory_management.repositories.InventoryRecordRepository;
import com.ug.ug_inventory_management.repositories.InventoryValueRepository;
import com.ug.ug_inventory_management.repositories.TemplateFieldRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRecordRepository recordRepo;
    private final InventoryValueRepository valueRepo;
    private final TemplateFieldRepository fieldRepo;

    public InventoryService(InventoryRecordRepository r,
                            InventoryValueRepository v,
                            TemplateFieldRepository f) {
        this.recordRepo = r;
        this.valueRepo = v;
        this.fieldRepo = f;
    }

    @Transactional
    public void saveRecord(InventoryRequest request) {

        InventoryRecord record = new InventoryRecord();
        record.setTemplateId(request.getTemplateId());
        record.setUnitId(request.getUnitId());
        recordRepo.save(record);

        List<TemplateField> fields =
                fieldRepo.findByTemplateId(request.getTemplateId());

        for (TemplateField field : fields) {

            String value = request.getValues().get(field.getFieldName());

            if (value == null) {
                throw new RuntimeException(
                        "Missing value for field: " + field.getFieldName()
                );
            }

            InventoryValue v = new InventoryValue();
            v.setRecordId(record.getId());
            v.setFieldId(field.getId());
            v.setValue(value);

            valueRepo.save(v);
        }
    }


    public List<Map<String, String>> getInventory(Long templateId, Long unitId) {

        List<InventoryRecord> records =
                recordRepo.findByTemplateIdAndUnitId(templateId, unitId);

        List<TemplateField> fields =
                fieldRepo.findByTemplateId(templateId);

        Map<Long, String> fieldMap = fields.stream()
                .collect(Collectors.toMap(
                        TemplateField::getId,
                        TemplateField::getFieldName
                ));

        List<Map<String, String>> result = new ArrayList<>();

        for (InventoryRecord record : records) {

            List<InventoryValue> values =
                    valueRepo.findByRecordId(record.getId());

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
                recordRepo.findByTemplateId(templateId);

        List<TemplateField> fields =
                fieldRepo.findByTemplateId(templateId);

        Map<Long, String> fieldMap = fields.stream()
                .collect(Collectors.toMap(
                        TemplateField::getId,
                        TemplateField::getFieldName
                ));


        Map<Long, InventoryRecord> latestRecordPerUnit = new HashMap<>();

        for (InventoryRecord record : records) {
            Long unitId = record.getUnitId();

            if (!latestRecordPerUnit.containsKey(unitId) ||
                    record.getId() > latestRecordPerUnit.get(unitId).getId()) {
                latestRecordPerUnit.put(unitId, record);
            }
        }

        List<Map<String, String>> result = new ArrayList<>();

        for (Map.Entry<Long, InventoryRecord> entry : latestRecordPerUnit.entrySet()) {
            Long unitId = entry.getKey();
            InventoryRecord record = entry.getValue();

            List<InventoryValue> values =
                    valueRepo.findByRecordId(record.getId());

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
                recordRepo.findByTemplateId(templateId);

        List<TemplateField> fields =
                fieldRepo.findByTemplateId(templateId);

        List<Map<String, String>> result = new ArrayList<>();

        for (InventoryRecord record : records) {

            List<InventoryValue> values =
                    valueRepo.findByRecordId(record.getId());

            Map<Long, String> valueMap = new HashMap<>();
            for (InventoryValue val : values) {
                valueMap.put(val.getFieldId(), val.getValue());
            }

            Map<String, String> row = new LinkedHashMap<>();

            // ✅ ADD UNIT INFO (VERY IMPORTANT)
            row.put("unitId", String.valueOf(record.getUnitId()));

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
}