package com.instantsolutions.larimarpharma.service;



import com.instantsolutions.larimarpharma.DTOs.VisitExcelExportRequest;
import com.instantsolutions.larimarpharma.entity.ManagerVisit;
import com.instantsolutions.larimarpharma.entity.Visit;
import com.instantsolutions.larimarpharma.repository.ManagerVisitRepository;
import com.instantsolutions.larimarpharma.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelExportService {

    private final VisitRepository visitRepository;
    private final ManagerVisitRepository managerVisitRepository;

    private static final String[] HEADERS = {
            "Visit ID", "Visit Date", "Week Number", "Day of Week", "Status", "Visit Type",
            "Field Executive Name", "Field Executive Code",
            "Doctor Name", "Doctor Specialization", "Doctor Contact",
            "Pharmacy Name", "Pharmacy Location", "Contact Person", "Contact Number",
            "Stockist Name", "Stockist Type", "Order Value",
            "Actual Visit Time", "Location", "Notes", "Activities Performed",
            "Scheduled Date", "Actual Date", "Created At", "Updated At",
            "Manager Visit Status", "Slot Change Requests Count", "Converted Products Count"
    };

    private static final String[] COLUMN_KEYS = {
            "id", "visitDate", "weekNumber", "dayOfWeek", "status", "visitType",
            "fieldExecutiveName", "fieldExecutiveCode",
            "doctorName", "doctorSpecialization", "doctorContact",
            "pharmacyName", "pharmacyLocation", "contactPerson", "contactNumber",
            "stockistName", "stockistType", "orderValue",
            "actualVisitTime", "location", "notes", "activitiesPerformed",
            "scheduledDate", "actualDate", "createdAt", "updatedAt",
            "managerVisitStatus", "slotChangeRequestsCount", "convertedProductsCount"
    };

    @Transactional
    public ByteArrayInputStream exportVisitsToExcel(VisitExcelExportRequest request) {
        List<Visit> visits = fetchVisits(request);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Visits Report");

            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // Create data rows
            int rowNum = 1;
            for (Visit visit : visits) {
                Row row = sheet.createRow(rowNum++);
                populateRowData(row, visit);
            }

            // Auto-size all columns
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
                // Set minimum width to avoid too narrow columns
                if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            log.error("Failed to create Excel file", e);
            throw new RuntimeException("Failed to create Excel file", e);
        }
    }

    @Transactional
    public ByteArrayInputStream exportManagerVisitsToExcel(VisitExcelExportRequest request) {
        List<ManagerVisit> visits = fetchManagerVisits(request);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Visits Report");

            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // Create data rows
            int rowNum = 1;
            for (ManagerVisit visit : visits) {
                Row row = sheet.createRow(rowNum++);
                populateManagerRowData(row, visit);
            }

            // Auto-size all columns
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
                // Set minimum width to avoid too narrow columns
                if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            log.error("Failed to create Excel file", e);
            throw new RuntimeException("Failed to create Excel file", e);
        }
    }

    private List<Visit> fetchVisits(VisitExcelExportRequest request) {
        Specification<Visit> spec = buildSpecification(request);
        return visitRepository.findAll(spec);
    }

    private Specification<Visit> buildSpecification(VisitExcelExportRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Date range filter (required)
            if (request.getStartDate() != null && request.getEndDate() != null) {
                predicates.add(criteriaBuilder.between(
                        root.get("visitDate"),
                        request.getStartDate(),
                        request.getEndDate()
                ));
            } else if (request.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("visitDate"),
                        request.getStartDate()
                ));
            } else if (request.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("visitDate"),
                        request.getEndDate()
                ));
            }

            // Field Executive filter (optional)
            if (request.getFieldExecutiveId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("fieldExecutive").get("id"),
                        request.getFieldExecutiveId()
                ));
            }

            // Status filter (optional)
            if (request.getVisitStatus() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"),
                        request.getVisitStatus()
                ));
            }

            if (request.getCategory() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("doctor").get("category"),
                        request.getCategory()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<ManagerVisit> buildManagerSpecification(VisitExcelExportRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Date range filter (required)
            if (request.getStartDate() != null && request.getEndDate() != null) {
                predicates.add(criteriaBuilder.between(
                        root.get("visitDate"),
                        request.getStartDate(),
                        request.getEndDate()
                ));
            } else if (request.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("visitDate"),
                        request.getStartDate()
                ));
            } else if (request.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("visitDate"),
                        request.getEndDate()
                ));
            }

            // Manager filter (optional)
            if (request.getFieldExecutiveId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("manager").get("id"),
                        request.getManagerId()
                ));
            }

            // Status filter (optional)
            if (request.getVisitStatus() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"),
                        request.getVisitStatus()
                ));
            }

            if (request.getCategory() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("doctorCategory"),
                        request.getCategory()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<ManagerVisit> fetchManagerVisits(VisitExcelExportRequest request) {
        Specification<ManagerVisit> spec = buildManagerSpecification(request);
        return managerVisitRepository.findAll(spec);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private void populateRowData(Row row, Visit visit) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        int col = 0;

        // Basic Info
        setCellValue(row, col++, visit.getId());
        setCellValue(row, col++, visit.getVisitDate() != null ? visit.getVisitDate().format(dateFormatter) : "");
        setCellValue(row, col++, visit.getWeekNumber());
        setCellValue(row, col++, visit.getDayOfWeek());
        setCellValue(row, col++, visit.getStatus() != null ? visit.getStatus().name() : "");
        setCellValue(row, col++, visit.getVisitType() != null ? visit.getVisitType().name() : "");

        // Field Executive Info
        if (visit.getFieldExecutive() != null) {
            setCellValue(row, col++, visit.getFieldExecutive().getName());
            setCellValue(row, col++, visit.getFieldExecutive().getEmployeeCode());
        } else {
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
        }

        // Doctor Info
        if (visit.getDoctor() != null) {
            setCellValue(row, col++, visit.getDoctor().getName());
            setCellValue(row, col++, visit.getDoctor().getDesignation());
            setCellValue(row, col++, visit.getDoctor().getContactNumber());
        } else {
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
        }

        // Pharmacy Info
        setCellValue(row, col++, visit.getPharmacyName());
        setCellValue(row, col++, visit.getLocation());
        setCellValue(row, col++, visit.getContactPerson());
        setCellValue(row, col++, visit.getContactNumber());

        // Stockist Info
        setCellValue(row, col++, visit.getStockistName());
        setCellValue(row, col++, visit.getStockistType() != null ? visit.getStockistType().name() : "");
        setCellValue(row, col++, visit.getOrderValue());

        // Visit Details
        setCellValue(row, col++, visit.getActualVisitTime() != null ? visit.getActualVisitTime().format(dateTimeFormatter) : "");
        setCellValue(row, col++, visit.getLocation());
        setCellValue(row, col++, visit.getNotes());
        setCellValue(row, col++, visit.getActivitiesPerformed() != null ? String.join(", ", visit.getActivitiesPerformed()) : "");

        // Dates
        setCellValue(row, col++, visit.getScheduledDate() != null ? visit.getScheduledDate().format(dateTimeFormatter) : "");
        setCellValue(row, col++, visit.getActualDate() != null ? visit.getActualDate().format(dateTimeFormatter) : "");
        setCellValue(row, col++, visit.getCreatedAt() != null ? visit.getCreatedAt().format(dateTimeFormatter) : "");
        setCellValue(row, col++, visit.getUpdatedAt() != null ? visit.getUpdatedAt().format(dateTimeFormatter) : "");

        // Manager Visit Info
        setCellValue(row, col++, visit.getManagerVisit() != null && visit.getManagerVisit().getStatus() != null ?
                visit.getManagerVisit().getStatus().name() : "");

        // Counts
        setCellValue(row, col++, visit.getSlotChangeRequests() != null ? visit.getSlotChangeRequests().size() : 0);
        setCellValue(row, col++, visit.getConvertedProducts() != null ? visit.getConvertedProducts().size() : 0);
    }

    private void populateManagerRowData(Row row, ManagerVisit visit) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        int col = 0;

        // Basic Info
        setCellValue(row, col++, visit.getId());
        setCellValue(row, col++, visit.getVisitDate() != null ? visit.getVisitDate().format(dateFormatter) : "");
        setCellValue(row, col++, visit.getWeekNumber());
        setCellValue(row, col++, visit.getDayOfWeek());
        setCellValue(row, col++, visit.getStatus() != null ? visit.getStatus().name() : "");
        setCellValue(row, col++, visit.getVisitType() != null ? visit.getVisitType().name() : "");

        // Manager Info
        if (visit.getFieldExecutive() != null) {
            setCellValue(row, col++, visit.getManager().getName());
            setCellValue(row, col++, visit.getManager().getEmployeeCode());
        } else {
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
        }

        // Doctor Info
        if (visit.getOriginalVisit().getDoctor() != null) {
            setCellValue(row, col++, visit.getOriginalVisit().getDoctor().getName());
            setCellValue(row, col++, visit.getOriginalVisit().getDoctor().getDesignation());
            setCellValue(row, col++, visit.getOriginalVisit().getDoctor().getContactNumber());
        } else {
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
        }

        // Pharmacy Info
        setCellValue(row, col++, visit.getOriginalVisit().getPharmacyName());
        setCellValue(row, col++, visit.getOriginalVisit().getLocation());
        setCellValue(row, col++, visit.getOriginalVisit().getContactPerson());
        setCellValue(row, col++, visit.getOriginalVisit().getContactNumber());

        // Stockist Info
        setCellValue(row, col++, visit.getOriginalVisit().getStockistName());
        setCellValue(row, col++, visit.getOriginalVisit().getStockistType() != null ? visit.getOriginalVisit().getStockistType().name() : "");
        setCellValue(row, col++, visit.getOriginalVisit().getOrderValue());

        // Visit Details
        setCellValue(row, col++, visit.getOriginalVisit().getActualVisitTime() != null ? visit.getOriginalVisit().getActualVisitTime().format(dateTimeFormatter) : "");
        setCellValue(row, col++, visit.getOriginalVisit().getLocation());
        setCellValue(row, col++, visit.getManagerNotes());
        setCellValue(row, col++, visit.getActivitiesPerformed() != null ? String.join(", ", visit.getActivitiesPerformed()) : "");

        // Dates
        setCellValue(row, col++, visit.getScheduledDate() != null ? visit.getScheduledDate().format(dateTimeFormatter) : "");
        setCellValue(row, col++, visit.getOriginalVisit().getActualDate() != null ? visit.getOriginalVisit().getActualDate().format(dateTimeFormatter) : "");
        setCellValue(row, col++, visit.getCreatedAt() != null ? visit.getCreatedAt().format(dateTimeFormatter) : "");
        setCellValue(row, col++, visit.getOriginalVisit().getUpdatedAt() != null ? visit.getOriginalVisit().getUpdatedAt().format(dateTimeFormatter) : "");

        // Manager Visit Info
        setCellValue(row, col++, visit.getOriginalVisit().getManagerVisit() != null && visit.getOriginalVisit().getManagerVisit().getStatus() != null ?
                visit.getOriginalVisit().getManagerVisit().getStatus().name() : "");

        // Counts
        setCellValue(row, col++, visit.getSlotChangeRequests() != null ? visit.getSlotChangeRequests().size() : 0);
        setCellValue(row, col++, visit.getOriginalVisit().getConvertedProducts() != null ? visit.getOriginalVisit().getConvertedProducts().size() : 0);
    }


    private void setCellValue(Row row, int col, Object value) {
        Cell cell = row.createCell(col);
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
            CellStyle style = row.getSheet().getWorkbook().createCellStyle();
            style.setDataFormat(row.getSheet().getWorkbook().createDataFormat().getFormat("#,##0.00"));
            cell.setCellStyle(style);
        } else {
            cell.setCellValue(value.toString());
        }
    }
}
