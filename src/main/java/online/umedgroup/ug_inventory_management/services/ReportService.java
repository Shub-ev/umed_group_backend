package online.umedgroup.ug_inventory_management.services;

import online.umedgroup.ug_inventory_management.repositories.ReportRepository;
import online.umedgroup.ug_inventory_management.repositories.TemplateRepository;
import online.umedgroup.ug_inventory_management.common.dtos.ReportRequestDTO;
import online.umedgroup.ug_inventory_management.common.dtos.ReportResponseDTO;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final TemplateRepository templateRepo;

    public ReportService(ReportRepository reportRepository, TemplateRepository templateRepo) {
        this.reportRepository = reportRepository;
        this.templateRepo = templateRepo;
    }

    // =========================
    // MAIN FIELD WISE REPORT (FINAL FIXED)
    // =========================
    public List<ReportResponseDTO> getReport(ReportRequestDTO req) {

        LocalDateTime to = req.getToDate();
        LocalDateTime from = req.getFromDate();

        if (to == null) to = LocalDateTime.now();
        if (from == null) from = to.minusDays(7);

        if (from.isAfter(to)) {
            LocalDateTime temp = from;
            from = to;
            to = temp;
        }

        String unit = (req.getUnit() == null || req.getUnit().isBlank())
                ? null
                : req.getUnit().trim();

        String mainField = (req.getMainField() == null || req.getMainField().isBlank())
                ? null
                : req.getMainField().trim();

        Long templateId = null;

        if (req.getTemplateName() != null && !req.getTemplateName().isBlank()) {
            templateId = templateRepo
                    .findByTemplateNameIgnoreCase(req.getTemplateName().trim())
                    .map(t -> t.getId())
                    .orElse(null);

            if (templateId == null) return List.of();

        } else if (req.getTemplateId() != null && req.getTemplateId() > 0) {
            templateId = req.getTemplateId();
        }

        // ✅ FIXED CALL (5 parameters)
        List<Object[]> rows = reportRepository.getMainFieldReport(
                from,
                to,
                unit,
                mainField,
                templateId
        );

        return rows.stream().map(r -> {
            ReportResponseDTO dto = new ReportResponseDTO();

            dto.setUnit((String) r[0]);
            dto.setTemplateId(((Number) r[1]).longValue());
            dto.setTemplateName((String) r[2]);
            dto.setMainFieldValue((String) r[3]);

            dto.setTotalInward(r[4] != null ? ((Number) r[4]).longValue() : 0);
            dto.setTotalOutward(r[5] != null ? ((Number) r[5]).longValue() : 0);
            dto.setStock(r[6] != null ? ((Number) r[6]).longValue() : 0);

            return dto;
        }).toList();
    }
    // =========================
    // EXCEL EXPORT
    // =========================
    public void exportToExcel(List<ReportResponseDTO> data,
                              HttpServletResponse response,
                              String title) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=report.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Report");

        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue(title);

        Row header = sheet.createRow(1);
        header.createCell(0).setCellValue("Unit");
        header.createCell(1).setCellValue("Template");
        header.createCell(2).setCellValue("Main Field");
        header.createCell(3).setCellValue("Inward");
        header.createCell(4).setCellValue("Outward");
        header.createCell(5).setCellValue("Stock");

        int rowNum = 2;

        for (ReportResponseDTO r : data) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(r.getUnit());
            row.createCell(1).setCellValue(r.getTemplateName());
            row.createCell(2).setCellValue(r.getMainFieldValue());
            row.createCell(3).setCellValue(r.getTotalInward());
            row.createCell(4).setCellValue(r.getTotalOutward());
            row.createCell(5).setCellValue(r.getStock());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // =========================
    // PDF EXPORT
    // =========================
    public void exportToPdf(List<ReportResponseDTO> data,
                            HttpServletResponse response,
                            String title) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=report.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        document.add(new Paragraph(title + "\n\n"));

        PdfPTable table = new PdfPTable(6);

        table.addCell("Unit");
        table.addCell("Template");
        table.addCell("Main Field");
        table.addCell("Inward");
        table.addCell("Outward");
        table.addCell("Stock");

        for (ReportResponseDTO r : data) {

            table.addCell(r.getUnit());
            table.addCell(r.getTemplateName());
            table.addCell(r.getMainFieldValue());
            table.addCell(String.valueOf(r.getTotalInward()));
            table.addCell(String.valueOf(r.getTotalOutward()));
            table.addCell(String.valueOf(r.getStock()));
        }

        document.add(table);
        document.close();
    }
}