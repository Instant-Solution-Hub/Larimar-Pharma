package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.repository.*;
import com.instantsolutions.larimarpharma.utils.DateUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;

import static com.instantsolutions.larimarpharma.utils.DateUtil.calculateVisitDate;
import static com.instantsolutions.larimarpharma.utils.DateUtil.calculateVisitDateCurrentMonth;

@Service
@RequiredArgsConstructor
public class ZsmService {
    private final AdminRepository adminRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final VisitRepository visitRepository;
    private final ZsmVisitRepository zsmVisitRepository;
    private final DoctorRepository doctorRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void assignZsmToVisit(AssignZsmVisitRequest request) {

        LocalDate visitDate = DateUtil
                .calculateVisitDateCurrentMonth(request.getWeekNumber(),request.getDayOfWeek());
        // Remove existing assignments (if any)
        List<ZsmVisit> existingAssignments =
                zsmVisitRepository.findByZsmAdminIdAndVisitDate(
                        request.getZsmId(),
                        visitDate
                );

        for (ZsmVisit mv : existingAssignments) {
            Visit originalVisit = mv.getOriginalVisit();

            if (originalVisit != null) {
                originalVisit.setZsmVisit(null);
            }

            zsmVisitRepository.delete(mv);
        }

        // Fetch new FE visits
        List<Visit> visits = visitRepository.findEligibleManagerVisits(
                request.getFieldExecutiveId(),
                visitDate
        );

        System.out.println("VISIT DATE:"+visitDate);
        if (visits.isEmpty()) {

            throw new IllegalStateException("No A+ or A visits found");
        }

        Admin zsmAdmin = adminRepository.findById(request.getZsmId())
                .orElseThrow(() -> new EntityNotFoundException("ZSM not found"));

        FieldExecutive fe = fieldExecutiveRepository.findById(request.getFieldExecutiveId())
                .orElseThrow(() -> new EntityNotFoundException("FE not found"));

        //  Assign new visits
        for (Visit visit : visits) {

            System.out.println("VISIT ID: "+visit.getId());
            if (visit.getManagerVisit() != null) {
                continue;
            }

            ZsmVisit zv = ZsmVisit.builder()
                    .zsmAdmin(zsmAdmin)
                    .fieldExecutive(fe)
                    .originalVisit(visit)

                    // snapshot
                    .visitDate(visit.getVisitDate())
                    .weekNumber(visit.getWeekNumber())
                    .dayOfWeek(visit.getDayOfWeek())
                    .visitType(visit.getVisitType())
                    .status(visit.getStatus())
                    .scheduledDate(visit.getScheduledDate())

                    .doctorId(visit.getDoctor().getId())
                    .doctorName(visit.getDoctor().getName())
                    .doctorDesignation(visit.getDoctor().getDesignation())
                    .doctorCategory(visit.getDoctor().getCategory())
                    .hospitalName(visit.getDoctor().getHospitalName())
                    .build();

            zsmVisitRepository.save(zv);
            visit.setZsmVisit(zv);
        }


    }

    @Transactional
    public void unassignFieldExecutiveVisits(
            Long fieldExecutiveId,
            Integer weekNumber,
            Integer dayOfWeek
    ) {

        LocalDate visitDate = DateUtil
                .calculateVisitDateCurrentMonth(weekNumber,dayOfWeek);
        List<ZsmVisit> zsmVisits =
                zsmVisitRepository
                        .findByFieldExecutiveIdAndVisitDate(
                                fieldExecutiveId,
                                visitDate
                        );

        if (zsmVisits.isEmpty()) {
            return;
        }

        for (ZsmVisit zv : zsmVisits) {
            Visit originalVisit = zv.getOriginalVisit();

            if (originalVisit != null) {
                originalVisit.setZsmVisit(null);
            }
        }

        zsmVisitRepository.deleteAll(zsmVisits);
    }


    @Transactional
    public ZsmVisitDto markVisit(MarkVisitRequestDto dto) {
        ZsmVisit visit = zsmVisitRepository.findById(dto.getVisitId())
                .orElseThrow(() -> new EntityNotFoundException("Visit not found"));

        Doctor doctor = visit.getOriginalVisit().getDoctor();
//        visit.setActualDate(LocalDateTime.now());
        visit.setJoinedAt(LocalDateTime.now());

        //  Update visit fields
        visit.setStatus(dto.getStatus());
        visit.setManagerNotes(dto.getNotes());
        visit.setActivitiesPerformed(dto.getActivitiesPerformed());
        if (dto.getProductId() != null) {
            // Product detailing was done - associate product with manager visit
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + dto.getProductId()));

            // Set product relationship
            visit.setProduct(product);

        } else {
            // No product detailing done - clear product association
            visit.setProduct(null);
        }
        ZsmVisit savedVisit = zsmVisitRepository.save(visit);
        return mapToDto(savedVisit);
    }

    @Transactional
    public ZsmVisitDto reMarkVisit(MarkVisitRequestDto dto) {

        ZsmVisit visit = zsmVisitRepository.findById(dto.getVisitId())
                .orElseThrow(() -> new EntityNotFoundException("Visit not found"));

        // Only MISSED visits can be re-marked
        if (visit.getStatus() != Visit.VisitStatus.MISSED) {
            throw new IllegalStateException(
                    "Only missed visits can be re-marked"
            );
        }

        // Re-marking allowed only to COMPLETED
        if (dto.getStatus() != Visit.VisitStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Re-marked visit must be completed"
            );
        }


        Doctor doctor = visit.getOriginalVisit().getDoctor();
        LocalDate visitDate = visit.getVisitDate();

        boolean alreadyCompleted =
                zsmVisitRepository
                        .existsByZsmAdminIdAndDoctorIdAndVisitDateAndStatusAndIdNot(
                                visit.getZsmAdmin().getId(),
                                doctor.getId(),
                                visitDate,
                                Visit.VisitStatus.COMPLETED,
                                visit.getId()
                        );

        if (alreadyCompleted) {
            throw new IllegalStateException(
                    "Doctor already has a completed visit for this day"
            );
        }


        // Update visit (same fields as markVisit)
        visit.setStatus(Visit.VisitStatus.COMPLETED);
//        visit.setActualDate(LocalDateTime.now());
        visit.setJoinedAt(LocalDateTime.now());
        visit.setManagerNotes(dto.getNotes());
        visit.setActivitiesPerformed(dto.getActivitiesPerformed());
        if (dto.getProductId() != null) {
            // Product detailing was done - associate product with manager visit
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + dto.getProductId()));

            // Set product relationship
            visit.setProduct(product);

        } else {
            // No product detailing done - clear product association
            visit.setProduct(null);
        }
        ZsmVisit savedVisit = zsmVisitRepository.save(visit);
        return mapToDto(savedVisit);
    }

    public List<TodayScheduledVisitDto> getTodaysVisits(Long feId) {
        LocalDate today = LocalDate.now();

        LocalDateTime start = today.minusDays(2).atStartOfDay();   // Feb 17 00:00
        LocalDateTime end   = today.plusDays(1).atStartOfDay();    // Feb 20 00:00

        List<ZsmVisit> visits = zsmVisitRepository.findTodaysVisitsByZsm(
                start,
                end,
                feId
        );

        return visits.stream()
                .map(this::toTodayScheduledVisitDTO)
                .toList();
    }

    public List<TodayScheduledVisitDto> getTodaysAndMissedVisits(Long feId) {
        LocalDate today = LocalDate.now();

        LocalDateTime start = today.minusDays(2).atStartOfDay();   // Feb 17 00:00
        LocalDateTime end   = today.plusDays(1).atStartOfDay();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();// Feb 20 00:00

        List<ZsmVisit> visits = zsmVisitRepository.findTodaysAndMissedVisitsByZsm(
                start,
                end,
                monthStart,
                feId
        );

        return visits.stream()
                .map(this::toTodayScheduledVisitDTO)
                .toList();
    }

    public List<TodayScheduledVisitDto> getTodaysVisitsScheduledOnly(Long zsmId) {
        LocalDate today = LocalDate.now();

        List<ZsmVisit> visits = zsmVisitRepository.findTodayScheduledVisitsByZsm(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay(),
                Visit.VisitStatus.SCHEDULED,
                zsmId
        );

        return visits.stream()
                .map(this::toTodayScheduledVisitDTO)
                .toList();
    }

    public List<CompletedVisitDto> getCompletedVisits(Long zsmId) {

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        return zsmVisitRepository
                .findAllCompletedZsmVisits(zsmId, startOfMonth, endOfMonth)
                .stream()
                .map(this::mapToCompletedVisitDto)
                .toList();
    }

    public List<CompletedVisitDto> getMissedVisits(Long fieldExecutiveId) {

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        return zsmVisitRepository
                .findAllMissedZsmVisits(fieldExecutiveId, startOfMonth, endOfMonth)
                .stream()
                .map(this::mapToCompletedVisitDto)
                .toList();
    }

    public List<CompletedVisitDto> getAllMissedVisits() {

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        return zsmVisitRepository
                .findAllMissedZsmVisits(startOfMonth, endOfMonth)
                .stream()
                .map(this::mapToCompletedVisitDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public VisitComplianceResponse getZsmVisitCompliance(
            Long zsmId,
            String weekFilter
    ) {

        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();
        LocalDateTime endOfMonth = lastDayOfMonth.atTime(LocalTime.MAX);

        Integer weekNumber = null;
        if (!"all".equalsIgnoreCase(weekFilter) && weekFilter.startsWith("week")) {
            weekNumber = Integer.parseInt(weekFilter.replace("week", ""));
        }

        List<ZsmVisit> visits =
                zsmVisitRepository.findZsmComplianceRecords(
                        zsmId,
                        weekNumber,
                        startOfMonth,
                        endOfMonth
                );

        List<ComplianceRecordDto> records = visits.stream()
                .map(this::convertToZsmComplianceRecordDto)
                .toList();

        ComplianceStatsDto stats =
                calculateZsmComplianceStats(visits);

        int totalWeeks = calculateWeeksInMonth(now);

        return VisitComplianceResponse.builder()
                .stats(stats)
                .records(records)
                .totalWeeks(totalWeeks)
                .build();
    }


    private ComplianceRecordDto convertToZsmComplianceRecordDto(
            ZsmVisit visit
    ) {
        return ComplianceRecordDto.builder()
                .id(String.valueOf(visit.getId()))
                .name(visit.getDoctorName())
                .category(
                        visit.getDoctorCategory() != null
                                ? visit.getDoctorCategory().name()
                                : "N/A"
                )
                .scheduledDate(
                        visit.getScheduledDate() != null
                                ? visit.getScheduledDate().toLocalDate()
                                : visit.getVisitDate()
                )
                .status(visit.getStatus().name().toLowerCase())
                .week(visit.getWeekNumber())
                .visitType("doctor")
                .reason(null) // manager visits don't have missed reason
                .build();
    }

    private ComplianceStatsDto calculateZsmComplianceStats(
            List<ZsmVisit> visits
    ) {
        int scheduled = visits.size();

        int completed = (int) visits.stream()
                .filter(v -> v.getStatus() == Visit.VisitStatus.COMPLETED)
                .count();

        int missed = (int) visits.stream()
                .filter(v -> v.getStatus() == Visit.VisitStatus.MISSED)
                .count();

        int complianceRate =
                scheduled > 0
                        ? Math.round((completed * 100) / scheduled)
                        : 0;

        return ComplianceStatsDto.builder()
                .scheduled(scheduled)
                .completed(completed)
                .missed(missed)
                .complianceRate(complianceRate)
                .doctorVisits(scheduled)
                .doctorCompleted(completed)
                .pharmacistVisits(0)
                .pharmacistCompleted(0)
                .build();
    }

    private int calculateWeeksInMonth(LocalDate date) {
        LocalDate firstDay = date.withDayOfMonth(1);
        LocalDate lastDay = date.withDayOfMonth(date.lengthOfMonth());

        // Calculate week numbers (assuming ISO week definition)
        int firstWeek = firstDay.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int lastWeek = lastDay.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        if (firstWeek > lastWeek && lastDay.getYear() > firstDay.getYear()) {
            // Cross-year case
            lastWeek += 52;
        }

        return lastWeek - firstWeek + 1;
    }

    @Transactional(readOnly = true)
    public List<CompletedVisitDto> getVisitsForZsmWeekAndDay(
            Long zsmId,
            Integer weekNumber,
            Integer dayOfWeek
    ) {

        LocalDate chosenDate = calculateVisitDate(weekNumber, dayOfWeek);
        System.out.println("CHOOSEN DATE---------------"+chosenDate.toString());

        return zsmVisitRepository
                .findByZsmAndWeekAndDayForMonth(
                        zsmId,
                        chosenDate
                )
                .stream()
                .map(this::mapToCompletedVisitDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompletedVisitDto> getCurrentMonthVisitsForZsm(
            Long zsmId,
            Integer weekNumber,
            Integer dayOfWeek
    ) {
        LocalDate choosedDate = calculateVisitDateCurrentMonth(weekNumber, dayOfWeek);

        return zsmVisitRepository
                .findByZsmAndDate(
                        zsmId,
                        choosedDate
                )
                .stream()
                .map(this::mapToCompletedVisitDto)
                .toList();
    }

    // Services for admin

    @Transactional
    public CompletedVisitDto markZsmVisitAsCompleted(Long zsmVisitId) {

        ZsmVisit zsmVisit = zsmVisitRepository.findById(zsmVisitId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ZsmVisit not found with id: " + zsmVisitId
                ));

        if (zsmVisit.getStatus() == Visit.VisitStatus.COMPLETED) {
            throw new IllegalStateException("ZSM visit already completed");
        }

        if (zsmVisit.getStatus() == Visit.VisitStatus.REJECTED) {
            throw new IllegalStateException("Rejected visit cannot be completed");
        }

        zsmVisit.setStatus(Visit.VisitStatus.COMPLETED);
        zsmVisit.setManagerNotes("Visit marked as completed by admin");
        ZsmVisit updatedVisit = zsmVisitRepository.save(zsmVisit);
        return mapToCompletedVisitDto(updatedVisit);
    }


    @Transactional
    public VisitReportDto getZsmVisitReport(Long zsmId, LocalDate from, LocalDate to,
                                                String status, String category, String docType){
        VisitExcelExportRequest request = VisitExcelExportRequest.builder()
                .startDate(from)
                .endDate(to)
                .zsmId(zsmId)
                .visitStatus(status.equals("all") ? null : Visit.VisitStatus.valueOf(status))
                .category(category.equals("all") ? null : Doctor.Category.valueOf(category))
                .docType(docType.equals("all") ? null : Doctor.PracticeType.valueOf(docType))
                .build();

        Specification<ZsmVisit> spec = buildSpecification(request);
        List<ZsmVisit> fetchedVisits = zsmVisitRepository.findAll(spec);

        List<TodayScheduledVisitDto> mappedVisits = fetchedVisits.stream()
                .map(this::toTodayScheduledVisitDTO)
                .toList();

        long completedCount = fetchedVisits.stream()
                .filter(v -> v.getStatus() == Visit.VisitStatus.COMPLETED)
                .count();

        long missedCount = fetchedVisits.stream()
                .filter(v -> v.getStatus() == Visit.VisitStatus.MISSED)
                .count();

        long pendingCount = fetchedVisits.stream()
                .filter(v -> v.getStatus() == Visit.VisitStatus.SCHEDULED)
                .count();

        return VisitReportDto.builder()
                .completedVisitCount(completedCount)
                .missedVisitCount(missedCount)
                .pendingVisitCount(pendingCount)
                .visits(mappedVisits)
                .build();

    }

    private Specification<ZsmVisit> buildSpecification(VisitExcelExportRequest request) {
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

            // ZSM filter (optional)
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

            if (request.getDocType() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("originalVisit").get("doctor").get("practiceType"),
                        request.getDocType()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Transactional
    public ZsmVisitDto changeStatus(Long visitId, String status){
        ZsmVisit visit = zsmVisitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Visit not found"));
        visit.setStatus(Visit.VisitStatus.valueOf(status));
        return mapToDto(zsmVisitRepository.save(visit));

    }



    public ZsmVisitDto mapToDto(ZsmVisit visit) {
        return ZsmVisitDto.builder()
                .name(visit.getZsmAdmin().getName())
                .time(visit.getScheduledDate() != null
                        ? visit.getScheduledDate().toLocalTime().toString()
                        : null)
                .type(visit.getVisitType().toString())
                .feName(visit.getFieldExecutive() != null
                        ? visit.getFieldExecutive().getName()
                        : "Unassigned")
                .scheduledDate(visit.getScheduledDate())
                .build();
    }

    private TodayScheduledVisitDto toTodayScheduledVisitDTO(ZsmVisit zv) {

        FieldExecutive fe = zv.getFieldExecutive();
        Doctor originalDoctor = zv.getOriginalVisit() != null ? zv.getOriginalVisit().getDoctor() : null;

        return TodayScheduledVisitDto.builder()
                .visitId(zv.getId())
                .visitType(zv.getVisitType())
                .visitDate(zv.getVisitDate())
                .status(String.valueOf(zv.getStatus()))
                .latitude(null)
                .longitude(null)
                .locationMethod(null)
                .photoProofUrl(null)

                // Doctor snapshot fields
                .doctorId(zv.getDoctorId())
                .doctorName(zv.getDoctorName())
                .designation(zv.getDoctorDesignation()) // designation not stored in ZsmVisit
                .category(zv.getDoctorCategory() != null ? zv.getDoctorCategory().name() : null)
                .practiceType(originalDoctor != null && originalDoctor.getPracticeType() != null
                        ? originalDoctor.getPracticeType().toString()
                        : null) // practiceType not stored in ZsmVisit
                .hospital(zv.getHospitalName())

                // Pharmacy fields (not applicable in ZsmVisit snapshots)
                .pharmacyId(null)
                .pharmacyName(null)
                .contactPerson(null)
                .contactNumber(null)

                // Field Executive fields
                .fieldExecutiveId(fe != null ? fe.getId() : null)
                .fieldExecutiveName(fe != null ? fe.getName() : null)

                // Sequence fields (will be set later)
                .visitSequence(null)
                .sequenceLabel(null)
                .requiredVisits(null)
                .visitProgress(null)
                .isMinimumMet(false)
                .requirementStatus(null)
                .build();
    }

    private CompletedVisitDto mapToCompletedVisitDto(ZsmVisit v) {

        CompletedVisitDto.CompletedVisitDtoBuilder builder =
                CompletedVisitDto.builder()
                        .visitId(v.getId())
                        .visitType(v.getVisitType())
                        .status(v.getStatus())
                        .visitDate(v.getVisitDate())
                        .weekNumber(v.getWeekNumber())
                        .dayOfWeek(v.getDayOfWeek())
                        .actualVisitTime(v.getJoinedAt())
                        .location("")
                        .feName(v.getFieldExecutive()!=null ? v.getFieldExecutive().getName() : "")
                        .feEmpCode(v.getFieldExecutive()!=null ? v.getFieldExecutive().getEmployeeCode() : "")
                        .feId(v.getFieldExecutive()!=null ? v.getFieldExecutive().getId() : 0)
                        .zsmName(v.getZsmAdmin().getName())
                        .zsmEmpCode(v.getZsmAdmin().getEmployeeCode())
                        .userRole("ZSM")
                        .productId(v.getProduct() !=null ? v.getProduct().getId() : null)
                        .productName(v.getProduct() !=null ? v.getProduct().getName() : "")
                        .notes(v.getManagerNotes());

        // 🔁 Doctor mapping with fallback
        if (v.getOriginalVisit() != null && v.getOriginalVisit().getDoctor() != null) {
            builder.doctor(mapDoctor(v.getOriginalVisit().getDoctor()));
        } else {
            Doctor doc = doctorRepository.findById(v.getDoctorId()).orElseThrow(()-> new EntityNotFoundException("Doctor not found"));
            builder.doctor(
                    mapDoctor(doc)
            );
        }

        return builder.build();
    }

    private DoctorDetailsDto mapDoctor(Doctor d) {
        return DoctorDetailsDto.builder()
                .id(d.getId())
                .name(d.getName())
                .category(d.getCategory())
                .practiceType(d.getPracticeType())
                .designation(d.getDesignation())
                .hospitalName(d.getHospitalName())
                .location(d.getLocation())
                .contactNumber(d.getContactNumber())
                .doctorCode(d.getDoctorCode())
                .latitude(d.getLatitude())
                .longitude(d.getLongitude())
                .active(d.isActive())
                .build();
    }




}
