package online.umedgroup.ug_inventory_management.controllers;

import online.umedgroup.ug_inventory_management.common.dtos.ReportRequestDTO;
import online.umedgroup.ug_inventory_management.common.dtos.ReportResponseDTO;
import online.umedgroup.ug_inventory_management.services.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final Logger log = LoggerFactory.getLogger(ReportController.class);

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<List<ReportResponseDTO>> getReport(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String mainField,
            @RequestParam(required = false) Long templateId
    ) {
        log.info("Fetching Report from: {} to: {}", from, to);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime fromDate = null;
        LocalDateTime toDate = null;

        try {
            if (from != null && !from.isBlank()) {
                fromDate = LocalDateTime.parse(from, formatter);
            }
            if (to != null && !to.isBlank()) {
                toDate = LocalDateTime.parse(to, formatter);
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format. Expected ISO format.");
        }

        ReportRequestDTO req = new ReportRequestDTO();
        req.setFromDate(fromDate);
        req.setToDate(toDate);
        req.setUnit(unit);
        req.setMainField(mainField);
        req.setTemplateId(templateId);

        List<ReportResponseDTO> responseDTOList = reportService.getReport(req);
        log.info("Fetch report completed");
        return ResponseEntity.ok(responseDTOList);
    }

    @GetMapping("/export/excel")
    public void exportExcel(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String mainField,
            @RequestParam(required = false) String templateName,
            @RequestParam(required = false) Long templateId,
            @RequestParam(required = false) String title,
            HttpServletResponse response
    ) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        ReportRequestDTO req = new ReportRequestDTO();
        req.setFromDate(LocalDateTime.parse(from, formatter));
        req.setToDate(LocalDateTime.parse(to, formatter));
        req.setUnit(unit);
        req.setMainField(mainField);
        req.setTemplateName(templateName);
        req.setTemplateId(templateId);

        List<ReportResponseDTO> data = reportService.getReport(req);

        String finalTitle = (title != null && !title.isBlank())
                ? title
                : "Report from " + from + " to " + to;

        reportService.exportToExcel(data, response, finalTitle);
    }

    @GetMapping("/export/pdf")
    public void exportPdf(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String mainField,
            @RequestParam(required = false) String templateName,
            @RequestParam(required = false) Long templateId,
            @RequestParam(required = false) String title,
            HttpServletResponse response
    ) throws Exception {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        ReportRequestDTO req = new ReportRequestDTO();
        req.setFromDate(LocalDateTime.parse(from, formatter));
        req.setToDate(LocalDateTime.parse(to, formatter));
        req.setUnit(unit);
        req.setMainField(mainField);
        req.setTemplateName(templateName);
        req.setTemplateId(templateId);

        List<ReportResponseDTO> data = reportService.getReport(req);

        String finalTitle = (title != null && !title.isBlank())
                ? title
                : "Report from " + from + " to " + to;

        reportService.exportToPdf(data, response, finalTitle);
    }
}