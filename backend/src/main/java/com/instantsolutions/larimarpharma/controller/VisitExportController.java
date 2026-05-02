package com.instantsolutions.larimarpharma.controller;



import com.instantsolutions.larimarpharma.DTOs.VisitExcelExportRequest;
import com.instantsolutions.larimarpharma.entity.Visit;
import com.instantsolutions.larimarpharma.service.ExcelExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitExportController {

    private final ExcelExportService excelExportService;

    @PostMapping("/export/excel")
    public ResponseEntity<InputStreamResource> exportVisitsToExcel(@RequestBody VisitExcelExportRequest request) {

        // Validate dates
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        // Generate filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("visits_report_%s.xlsx", timestamp);

        InputStreamResource resource = new InputStreamResource(
                excelExportService.exportVisitsToExcel(request)
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    // Alternative GET endpoint for simpler usage
    @GetMapping("/export/excel")
    public ResponseEntity<InputStreamResource> exportVisitsToExcelGet(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long fieldExecutiveId,
            @RequestParam(required = false) Visit.VisitStatus visitStatus
    ) {
        VisitExcelExportRequest request = VisitExcelExportRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .fieldExecutiveId(fieldExecutiveId)
                .visitStatus(visitStatus)
                .build();
        return exportVisitsToExcel(request);
    }
}