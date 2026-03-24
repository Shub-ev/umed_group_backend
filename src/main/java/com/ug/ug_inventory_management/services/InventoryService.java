//package com.ug.ug_inventory_management.services;
//import java.util.*;
//import com.ug.ug_inventory_management.common.dtos.InventoryRequest;
//import com.ug.ug_inventory_management.models.*;
//import com.ug.ug_inventory_management.repositories.*;
//import jakarta.transaction.Transactional;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class InventoryService {
//
//    private final InventoryRecordRepository recordRepo;
//    private final InventoryValueRepository valueRepo;
//    private final TemplateFieldRepository fieldRepo;
//
//    public InventoryService(InventoryRecordRepository r,
//                            InventoryValueRepository v,
//                            TemplateFieldRepository f) {
//        this.recordRepo = r;
//        this.valueRepo = v;
//        this.fieldRepo = f;
//    }
//
//    @Transactional
//    public void saveRecord(InventoryRequest request) {
//
//        InventoryRecord record = new InventoryRecord();
//        record.setTemplateId(request.getTemplateId());
//        record.setUnitId(request.getUnitId());
//        recordRepo.save(record);
//
//        List<TemplateField> fields =
//                fieldRepo.findByTemplateId(request.getTemplateId());
//
//        for (TemplateField field : fields) {
//
//            String value = request.getValues().get(field.getFieldName());
//
//            InventoryValue v = new InventoryValue();
//            v.setRecordId(record.getId());
//            v.setFieldId(field.getId());
//            v.setValue(value);
//
//            valueRepo.save(v);
//        }
//    }
//
//    public List<Map<String, String>> getInventory(Long templateId, Long unitId) {
//
//        List<InventoryRecord> records =
//                recordRepo.findByTemplateIdAndUnitId(templateId, unitId);
//
//        List<TemplateField> fields =
//                fieldRepo.findByTemplateId(templateId);
//
//        List<Map<String, String>> result = new ArrayList<>();
//
//        for (InventoryRecord record : records) {
//
//            List<InventoryValue> values =
//                    valueRepo.findByRecordId(record.getId());
//
//            Map<String, String> row = new HashMap<>();
//
//            for (TemplateField field : fields) {
//                for (InventoryValue val : values) {
//                    if (val.getFieldId().equals(field.getId())) {
//                        row.put(field.getFieldName(), val.getValue());
//                    }
//                }
//            }
//
//            result.add(row);
//        }
//
//        return result;
//    }
//}


package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.common.dtos.CreateInventoryRecordDTO;
import com.ug.ug_inventory_management.models.InventoryRecord;
import com.ug.ug_inventory_management.models.InventoryValue;
import com.ug.ug_inventory_management.models.TemplateField;
import com.ug.ug_inventory_management.repositories.InventoryRecordRepository;
import com.ug.ug_inventory_management.repositories.InventoryValueRepository;
import com.ug.ug_inventory_management.repositories.TemplateFieldRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;

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

    // ✅ SAVE INVENTORY (CREATE)
    @Transactional
    public void saveRecord(@NotNull CreateInventoryRecordDTO request) {

        InventoryRecord record = new InventoryRecord();
        record.setTemplateId(request.getTemplateId());
        record.setUnitName(request.getUnitName());
        recordRepo.save(record);

        List<TemplateField> fields =
                fieldRepo.findByTemplateId(request.getTemplateId());

        for (TemplateField field : fields) {

            String value = request.getValues().get(field.getFieldName());

            // ✅ VALIDATION (important)
            if (value == null) {
                throw new RuntimeException(
                        "Missing value for field: " + field.getFieldName()
                );
            }

            InventoryValue v = new InventoryValue();
            v.setRecordId(record);
            v.setFieldId(field.getId());
            v.setValue(value);

            valueRepo.save(v);
        }
    }

    // ✅ FETCH INVENTORY (DYNAMIC TABLE)
    public List<Map<String, String>> getInventory(@NotNull Long templateId,@NotNull String unitName) {

        List<InventoryRecord> records =
                recordRepo.findByTemplateIdAndUnitName(templateId, unitName);

        List<TemplateField> fields =
                fieldRepo.findByTemplateId(templateId);

        List<Map<String, String>> result = new ArrayList<>();

        for (InventoryRecord record : records) {

            List<InventoryValue> values =
                    valueRepo.findByRecordId(record.getId());

            // 🔥 OPTIMIZATION (important)
            Map<Long, String> valueMap = new HashMap<>();
            for (InventoryValue val : values) {
                valueMap.put(val.getFieldId(), val.getValue());
            }

            Map<String, String> row = new LinkedHashMap<>();

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