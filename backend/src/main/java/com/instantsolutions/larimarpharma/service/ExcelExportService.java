package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.VisitExcelExportRequest;
import com.instantsolutions.larimarpharma.entity.Doctor;
import com.instantsolutions.larimarpharma.entity.ManagerVisit;
import com.instantsolutions.larimarpharma.entity.Visit;
import com.instantsolutions.larimarpharma.entity.ZsmVisit;
import com.instantsolutions.larimarpharma.repository.ManagerVisitRepository;
import com.instantsolutions.larimarpharma.repository.VisitRepository;
import com.instantsolutions.larimarpharma.repository.ZsmVisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelExportService {

    private final VisitRepository visitRepository;
    private final ManagerVisitRepository managerVisitRepository;
    private final ZsmVisitRepository zsmVisitRepository;

    private static final String[] HEADERS = {
            "Visit ID", "Visit Date", "Week Number", "Day of Week", "Status", "Visit Type",
            "Field Executive Name", "Field Executive Code",
            "Doctor Name", "Doctor Specialization", "Doctor Contact", "Doctor Category", "Doctor Practice Type",
            "Pharmacy Name", "Pharmacy Location", "Contact Person", "Contact Number",
            "Stockist Name", "Stockist Type", "Order Value",
            "Actual Visit Time", "Location", "Notes", "Activities Performed",
            "Scheduled Date", "Actual Date", "Created At", "Updated At",
            "Manager Visit Status", "Slot Change Requests Count", "Converted Products Count",
            "Visit Sequence", "Sequence Label", "Required Visits", "Visit Progress", "Requirement Status"
    };

    private static final String[] COLUMN_KEYS = {
            "id", "visitDate", "weekNumber", "dayOfWeek", "status", "visitType",
            "fieldExecutiveName", "fieldExecutiveCode",
            "doctorName", "doctorSpecialization", "doctorContact", "doctorCategory", "doctorPracticeType",
            "pharmacyName", "pharmacyLocation", "contactPerson", "contactNumber",
            "stockistName", "stockistType", "orderValue",
            "actualVisitTime", "location", "notes", "activitiesPerformed",
            "scheduledDate", "actualDate", "createdAt", "updatedAt",
            "managerVisitStatus", "slotChangeRequestsCount", "convertedProductsCount",
            "visitSequence", "sequenceLabel", "requiredVisits", "visitProgress", "requirementStatus"
    };

    @Transactional
    public ByteArrayInputStream exportVisitsToExcel(VisitExcelExportRequest request) {
        List<Visit> visits = fetchVisits(request);

        // Sort visits by date and time to ensure correct sequence
        List<Visit> sortedVisits = visits.stream()
                .sorted(Comparator.comparing(Visit::getVisitDate)
                        .thenComparing(Visit::getActualVisitTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

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

            // Calculate visit sequences for A and A+ doctors
            Map<Long, Integer> doctorVisitCounter = new HashMap<>();
            Map<Doctor.Category, Integer> requiredVisitsMap = Map.of(
                    Doctor.Category.A_PLUS, 3,
                    Doctor.Category.A, 2
            );

            // Create data rows
            int rowNum = 1;
            for (Visit visit : sortedVisits) {
                Row row = sheet.createRow(rowNum++);
                populateRowData(row, visit, doctorVisitCounter, requiredVisitsMap);
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

    @Transactional
    public ByteArrayInputStream exportZsmVisitsToExcel(VisitExcelExportRequest request) {
        List<ZsmVisit> visits = fetchZsmVisits(request);

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
            for (ZsmVisit visit : visits) {
                Row row = sheet.createRow(rowNum++);
                populateZsmRowData(row, visit);
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
            if (request.getManagerId() != null) {
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

    private Specification<ZsmVisit> buildZsmSpecification(VisitExcelExportRequest request) {
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
            if (request.getZsmId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("zsmAdmin").get("id"),
                        request.getZsmId()
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

    private List<ZsmVisit> fetchZsmVisits(VisitExcelExportRequest request) {
        Specification<ZsmVisit> spec = buildZsmSpecification(request);
        return zsmVisitRepository.findAll(spec);
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

    private void populateRowData(Row row, Visit visit,
                                 Map<Long, Integer> doctorVisitCounter,
                                 Map<Doctor.Category, Integer> requiredVisitsMap) {
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
            setCellValue(row, col++, visit.getDoctor().getCategory() != null ? visit.getDoctor().getCategory().name() : "");
            setCellValue(row, col++, visit.getDoctor().getPracticeType() != null ? visit.getDoctor().getPracticeType().name() : "");
        } else {
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
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

        // Visit Sequence Information
        // Only calculate sequence for A and A+ doctors
        if (visit.getDoctor() != null && isCategoryAOrAPlus(visit.getDoctor().getCategory())) {
            Long doctorId = visit.getDoctor().getId();
            int currentVisitNumber = doctorVisitCounter.getOrDefault(doctorId, 0) + 1;
            doctorVisitCounter.put(doctorId, currentVisitNumber);

            int requiredVisits = requiredVisitsMap.getOrDefault(visit.getDoctor().getCategory(), 0);

            setCellValue(row, col++, currentVisitNumber);
            setCellValue(row, col++, getSequenceLabel(currentVisitNumber));
            setCellValue(row, col++, requiredVisits);
            setCellValue(row, col++, currentVisitNumber + "/" + requiredVisits);
            setCellValue(row, col++, getRequirementStatus(currentVisitNumber, requiredVisits));
        } else {
            // For non-A/A+ doctors, set defaults
            setCellValue(row, col++, "");
            setCellValue(row, col++, "N/A");
            setCellValue(row, col++, "");
            setCellValue(row, col++, "N/A");
            setCellValue(row, col++, "N/A");
        }
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
        if (visit.getManager() != null) {
            setCellValue(row, col++, visit.getManager().getName());
            setCellValue(row, col++, visit.getManager().getEmployeeCode());
        } else {
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
        }

        // Doctor Info
        if (visit.getOriginalVisit() != null && visit.getOriginalVisit().getDoctor() != null) {
            setCellValue(row, col++, visit.getOriginalVisit().getDoctor().getName());
            setCellValue(row, col++, visit.getOriginalVisit().getDoctor().getDesignation());
            setCellValue(row, col++, visit.getOriginalVisit().getDoctor().getContactNumber());
            setCellValue(row, col++, visit.getDoctorCategory() != null ? visit.getDoctorCategory().name() : "");
//            setCellValue(row, col++, visit.getDoctorPracticeType() != null ? visit.getDoctorPracticeType().name() : "");
        } else {
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
        }

        // Pharmacy Info
        if (visit.getOriginalVisit() != null) {
            setCellValue(row, col++, visit.getOriginalVisit().getPharmacyName());
            setCellValue(row, col++, visit.getOriginalVisit().getLocation());
            setCellValue(row, col++, visit.getOriginalVisit().getContactPerson());
            setCellValue(row, col++, visit.getOriginalVisit().getContactNumber());
        } else {
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
        }

        // Stockist Info
        if (visit.getOriginalVisit() != null) {
            setCellValue(row, col++, visit.getOriginalVisit().getStockistName());
            setCellValue(row, col++, visit.getOriginalVisit().getStockistType() != null ? visit.getOriginalVisit().getStockistType().name() : "");
            setCellValue(row, col++, visit.getOriginalVisit().getOrderValue());
        } else {
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
        }

        // Visit Details
        if (visit.getOriginalVisit() != null) {
            setCellValue(row, col++, visit.getOriginalVisit().getActualVisitTime() != null ?
                    visit.getOriginalVisit().getActualVisitTime().format(dateTimeFormatter) : "");
            setCellValue(row, col++, visit.getOriginalVisit().getLocation());
        } else {
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
        }
        setCellValue(row, col++, visit.getManagerNotes());
        setCellValue(row, col++, visit.getActivitiesPerformed() != null ? String.join(", ", visit.getActivitiesPerformed()) : "");

        // Dates
        setCellValue(row, col++, visit.getScheduledDate() != null ? visit.getScheduledDate().format(dateTimeFormatter) : "");
        if (visit.getOriginalVisit() != null) {
            setCellValue(row, col++, visit.getOriginalVisit().getActualDate() != null ?
                    visit.getOriginalVisit().getActualDate().format(dateTimeFormatter) : "");
            setCellValue(row, col++, visit.getOriginalVisit().getUpdatedAt() != null ?
                    visit.getOriginalVisit().getUpdatedAt().format(dateTimeFormatter) : "");
        } else {
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
        }
        setCellValue(row, col++, visit.getCreatedAt() != null ? visit.getCreatedAt().format(dateTimeFormatter) : "");

        // Manager Visit Info
        if (visit.getOriginalVisit() != null && visit.getOriginalVisit().getManagerVisit() != null &&
                visit.getOriginalVisit().getManagerVisit().getStatus() != null) {
            setCellValue(row, col++, visit.getOriginalVisit().getManagerVisit().getStatus().name());
        } else {
            setCellValue(row, col++, "");
        }

        // Counts
        setCellValue(row, col++, visit.getSlotChangeRequests() != null ? visit.getSlotChangeRequests().size() : 0);
        if (visit.getOriginalVisit() != null) {
            setCellValue(row, col++, visit.getOriginalVisit().getConvertedProducts() != null ?
                    visit.getOriginalVisit().getConvertedProducts().size() : 0);
        } else {
            setCellValue(row, col++, 0);
        }

        // Visit Sequence Information - Not applicable for Manager Visits
        setCellValue(row, col++, "");
        setCellValue(row, col++, "N/A");
        setCellValue(row, col++, "");
        setCellValue(row, col++, "N/A");
        setCellValue(row, col++, "N/A");
    }

    private void populateZsmRowData(Row row, ZsmVisit visit) {
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
        if (visit.getZsmAdmin() != null) {
            setCellValue(row, col++, visit.getZsmAdmin().getName());
            setCellValue(row, col++, visit.getZsmAdmin().getEmployeeCode());
        } else {
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
        }

        // Doctor Info
        if (visit.getOriginalVisit() != null && visit.getOriginalVisit().getDoctor() != null) {
            setCellValue(row, col++, visit.getOriginalVisit().getDoctor().getName());
            setCellValue(row, col++, visit.getOriginalVisit().getDoctor().getDesignation());
            setCellValue(row, col++, visit.getOriginalVisit().getDoctor().getContactNumber());
            setCellValue(row, col++, visit.getDoctorCategory() != null ? visit.getDoctorCategory().name() : "");
//            setCellValue(row, col++, visit.getDoctorPracticeType() != null ? visit.getDoctorPracticeType().name() : "");
        } else {
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
        }

        // Pharmacy Info
        if (visit.getOriginalVisit() != null) {
            setCellValue(row, col++, visit.getOriginalVisit().getPharmacyName());
            setCellValue(row, col++, visit.getOriginalVisit().getLocation());
            setCellValue(row, col++, visit.getOriginalVisit().getContactPerson());
            setCellValue(row, col++, visit.getOriginalVisit().getContactNumber());
        } else {
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
        }

        // Stockist Info
        if (visit.getOriginalVisit() != null) {
            setCellValue(row, col++, visit.getOriginalVisit().getStockistName());
            setCellValue(row, col++, visit.getOriginalVisit().getStockistType() != null ? visit.getOriginalVisit().getStockistType().name() : "");
            setCellValue(row, col++, visit.getOriginalVisit().getOrderValue());
        } else {
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
        }

        // Visit Details
        if (visit.getOriginalVisit() != null) {
            setCellValue(row, col++, visit.getOriginalVisit().getActualVisitTime() != null ?
                    visit.getOriginalVisit().getActualVisitTime().format(dateTimeFormatter) : "");
            setCellValue(row, col++, visit.getOriginalVisit().getLocation());
        } else {
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
        }
        setCellValue(row, col++, visit.getManagerNotes());
        setCellValue(row, col++, visit.getActivitiesPerformed() != null ? String.join(", ", visit.getActivitiesPerformed()) : "");

        // Dates
        setCellValue(row, col++, visit.getScheduledDate() != null ? visit.getScheduledDate().format(dateTimeFormatter) : "");
        if (visit.getOriginalVisit() != null) {
            setCellValue(row, col++, visit.getOriginalVisit().getActualDate() != null ?
                    visit.getOriginalVisit().getActualDate().format(dateTimeFormatter) : "");
            setCellValue(row, col++, visit.getOriginalVisit().getUpdatedAt() != null ?
                    visit.getOriginalVisit().getUpdatedAt().format(dateTimeFormatter) : "");
        } else {
            setCellValue(row, col++, "");
            setCellValue(row, col++, "");
        }
        setCellValue(row, col++, visit.getCreatedAt() != null ? visit.getCreatedAt().format(dateTimeFormatter) : "");

        // Manager Visit Info
        if (visit.getOriginalVisit() != null && visit.getOriginalVisit().getManagerVisit() != null &&
                visit.getOriginalVisit().getManagerVisit().getStatus() != null) {
            setCellValue(row, col++, visit.getOriginalVisit().getManagerVisit().getStatus().name());
        } else {
            setCellValue(row, col++, "");
        }

        // Counts
        setCellValue(row, col++, visit.getSlotChangeRequests() != null ? visit.getSlotChangeRequests().size() : 0);
        if (visit.getOriginalVisit() != null) {
            setCellValue(row, col++, visit.getOriginalVisit().getConvertedProducts() != null ?
                    visit.getOriginalVisit().getConvertedProducts().size() : 0);
        } else {
            setCellValue(row, col++, 0);
        }

        // Visit Sequence Information - Not applicable for Manager Visits
        setCellValue(row, col++, "");
        setCellValue(row, col++, "N/A");
        setCellValue(row, col++, "");
        setCellValue(row, col++, "N/A");
        setCellValue(row, col++, "N/A");
    }

    private boolean isCategoryAOrAPlus(Doctor.Category category) {
        return category == Doctor.Category.A_PLUS || category == Doctor.Category.A;
    }

    private String getSequenceLabel(int number) {
        if (number == 1) return "1st Visit";
        if (number == 2) return "2nd Visit";
        if (number == 3) return "3rd Visit";
        return number + "th Visit";
    }

    private String getRequirementStatus(int current, int required) {
        if (current >= required) {
            return "Met";
        } else if (current == required - 1) {
            return "One more required";
        } else {
            return (required - current) + " more required";
        }
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