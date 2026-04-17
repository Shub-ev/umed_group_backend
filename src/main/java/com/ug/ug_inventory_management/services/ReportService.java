package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.repositories.ReportRepository;
import com.ug.ug_inventory_management.common.dtos.ReportRequestDTO;
import com.ug.ug_inventory_management.common.dtos.ReportResponseDTO;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class ReportService {

    private final ReportRepository repo;

    public ReportService(ReportRepository repo) {
        this.repo = repo;
    }

    public List<ReportResponseDTO> getReport(ReportRequestDTO req) {

        LocalDateTime to = req.getToDate();
        LocalDateTime from = req.getFromDate();

        // ✅ DEFAULT: last 7 days always safe
        if (to == null) {
            to = LocalDateTime.now();
        }

        if (from == null) {
            from = to.minusDays(7);
        }

        // safety: if user sends reversed dates
        if (from.isAfter(to)) {
            LocalDateTime temp = from;
            from = to;
            to = temp;
        }

        String unit = (req.getUnit() == null || req.getUnit().isBlank())
                ? null
                : req.getUnit();

        Long templateId = (req.getTemplateId() != null && req.getTemplateId() > 0)
                ? req.getTemplateId()
                : null;

        List<Object[]> rows = repo.getReport(from, to, unit, templateId);

        return rows.stream().map(r -> {
            ReportResponseDTO dto = new ReportResponseDTO();

            dto.setUnit((String) r[0]);
            dto.setTemplateId(((Number) r[1]).longValue());
            dto.setTemplateName((String) r[2]);

            dto.setTotalInward(r[3] != null ? ((Number) r[3]).longValue() : 0);
            dto.setTotalOutward(r[4] != null ? ((Number) r[4]).longValue() : 0);
            dto.setStock(r[5] != null ? ((Number) r[5]).longValue() : 0);

            return dto;
        }).toList();
    }


    public void exportToExcel(List<ReportResponseDTO> data, HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=report.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Report");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Unit");
        header.createCell(1).setCellValue("Template");
        header.createCell(2).setCellValue("Inward");
        header.createCell(3).setCellValue("Outward");
        header.createCell(4).setCellValue("Stock");

        // Data
        int rowNum = 1;
        for (ReportResponseDTO r : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(r.getUnit());
            row.createCell(1).setCellValue(r.getTemplateName());
            row.createCell(2).setCellValue(r.getTotalInward());
            row.createCell(3).setCellValue(r.getTotalOutward());
            row.createCell(4).setCellValue(r.getStock());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }


    public void exportToPdf(List<ReportResponseDTO> data, HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=report.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        document.add(new Paragraph("Inventory Report\n\n"));

        PdfPTable table = new PdfPTable(5);

        table.addCell("Unit");
        table.addCell("Template");
        table.addCell("Inward");
        table.addCell("Outward");
        table.addCell("Stock");

        for (ReportResponseDTO r : data) {
            table.addCell(r.getUnit());
            table.addCell(r.getTemplateName());
            table.addCell(String.valueOf(r.getTotalInward()));
            table.addCell(String.valueOf(r.getTotalOutward()));
            table.addCell(String.valueOf(r.getStock()));
        }

        document.add(table);
        document.close();
    }
}