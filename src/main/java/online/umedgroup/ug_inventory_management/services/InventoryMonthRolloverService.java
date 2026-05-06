package online.umedgroup.ug_inventory_management.services;

import online.umedgroup.ug_inventory_management.models.MonthRolloverLog;
import online.umedgroup.ug_inventory_management.models.InventoryRecord;
import online.umedgroup.ug_inventory_management.models.InventoryValue;
import online.umedgroup.ug_inventory_management.models.TemplateField;
import online.umedgroup.ug_inventory_management.repositories.InventoryRecordRepository;
import online.umedgroup.ug_inventory_management.repositories.InventoryValueRepository;
import online.umedgroup.ug_inventory_management.repositories.TemplateFieldRepository;
import online.umedgroup.ug_inventory_management.repositories.MonthRolloverLogRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventoryMonthRolloverService {

    private static final Logger log =
            LoggerFactory.getLogger(InventoryMonthRolloverService.class);

    private final InventoryRecordRepository inventoryRecordRepository;
    private final InventoryValueRepository inventoryValueRepository;
    private final TemplateFieldRepository templateFieldRepository;
    private final MonthRolloverLogRepository monthRolloverLogRepository;

    @Value("${inventory.rollover.enabled:true}")
    private boolean rolloverEnabled;

    //  Manual constructor (replaces Lombok)
    public InventoryMonthRolloverService(
            InventoryRecordRepository inventoryRecordRepository,
            InventoryValueRepository inventoryValueRepository,
            TemplateFieldRepository templateFieldRepository,
            MonthRolloverLogRepository monthRolloverLogRepository
    ) {
        this.inventoryRecordRepository = inventoryRecordRepository;
        this.inventoryValueRepository = inventoryValueRepository;
        this.templateFieldRepository = templateFieldRepository;
        this.monthRolloverLogRepository = monthRolloverLogRepository;
    }

    @Scheduled(cron = "${inventory.rollover.cron:0 0 0 1 * *}", zone = "${inventory.rollover.zone:Asia/Kolkata}")
    @Transactional
    public void rolloverAtMonthStart() {

        if (!rolloverEnabled) {
            log.info("Month rollover is disabled");
            return;
        }

        String rolloverKey = YearMonth.now(ZoneId.of("Asia/Kolkata")).toString();

        // Prevent duplicate execution
        try {
            monthRolloverLogRepository.saveAndFlush(
                    new MonthRolloverLog(rolloverKey, LocalDateTime.now())
            );
        } catch (DataIntegrityViolationException e) {
            log.info("Rollover already executed for {}", rolloverKey);
            return;
        }

        List<InventoryRecord> records = inventoryRecordRepository.findAll();

        for (InventoryRecord record : records) {

            List<InventoryValue> values =
                    inventoryValueRepository.findByInventoryRecord_Id(record.getId());

            if (values.isEmpty()) continue;

            Map<Long, String> fieldMap = templateFieldRepository
                    .findByTemplate_IdOrderByDisplayOrderAsc(record.getTemplate().getId())
                    .stream()
                    .collect(Collectors.toMap(
                            TemplateField::getId,
                            f -> normalizeKey(f.getFieldName())
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
                log.warn("Skipping record {} because stock fields missing", record.getId());
                continue;
            }

            int inward = safeParse(inwardField.getValue());
            int outward = safeParse(outwardField.getValue());

            int previousStock = inward - outward;

            // ✅rollover logic
            inwardField.setValue(String.valueOf(previousStock));
            outwardField.setValue("0");
            stockField.setValue(String.valueOf(previousStock));

            inventoryValueRepository.saveAll(values);

            log.info("Rollover done for recordId={}, stock={}", record.getId(), previousStock);
        }

        log.info("Month rollover completed for {}", rolloverKey);
    }

    private String normalizeKey(String key) {
        return key == null ? "" : key.trim().toLowerCase();
    }

    private int safeParse(String value) {
        try {
            return Integer.parseInt(value == null ? "0" : value.trim());
        } catch (Exception e) {
            return 0;
        }
    }
}