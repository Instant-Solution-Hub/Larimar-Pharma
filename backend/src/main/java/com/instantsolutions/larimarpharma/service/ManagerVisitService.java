package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.repository.*;
import com.instantsolutions.larimarpharma.utils.GeoUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.util.List;
import java.util.Optional;

import static com.instantsolutions.larimarpharma.utils.DateUtil.getStartOfTheMonth;

@Service
@RequiredArgsConstructor
@Transactional
public class ManagerVisitService {

    private final ManagerRepository managerRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final VisitRepository visitRepository;
    private final ManagerVisitRepository managerVisitRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    public void assignManagerToVisit(AssignManagerVisitRequest request) {

        // Remove existing assignments (if any)
        List<ManagerVisit> existingAssignments =
                managerVisitRepository.findByManagerIdAndWeekNumberAndDayOfWeek(
                        request.getManagerId(),
                        request.getWeekNumber(),
                        request.getDayOfWeek()
                );

        for (ManagerVisit mv : existingAssignments) {
            Visit originalVisit = mv.getOriginalVisit();

            if (originalVisit != null) {
                originalVisit.setManagerVisit(null);
            }

            managerVisitRepository.delete(mv);
        }

        // Fetch new FE visits
        List<Visit> visits = visitRepository.findEligibleManagerVisits(
                request.getFieldExecutiveId(),
                request.getWeekNumber(),
                request.getDayOfWeek()
        );

        if (visits.isEmpty()) {
            throw new IllegalStateException("No A+ or A visits found");
        }

        Manager manager = managerRepository.findById(request.getManagerId())
                .orElseThrow(() -> new EntityNotFoundException("Manager not found"));

        FieldExecutive fe = fieldExecutiveRepository.findById(request.getFieldExecutiveId())
                .orElseThrow(() -> new EntityNotFoundException("FE not found"));

        //  Assign new visits
        for (Visit visit : visits) {

            if (visit.getManagerVisit() != null) {
                continue;
            }

            ManagerVisit mv = ManagerVisit.builder()
                    .manager(manager)
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

            managerVisitRepository.save(mv);
            visit.setManagerVisit(mv);
        }
    }

    @Transactional
    public void unassignFieldExecutiveVisits(
            Long fieldExecutiveId,
            Integer weekNumber,
            Integer dayOfWeek
    ) {

        List<ManagerVisit> managerVisits =
                managerVisitRepository
                        .findByFieldExecutiveIdAndWeekNumberAndDayOfWeek(
                                fieldExecutiveId,
                                weekNumber,
                                dayOfWeek
                        );

        if (managerVisits.isEmpty()) {
            return;
        }

        for (ManagerVisit mv : managerVisits) {
            Visit originalVisit = mv.getOriginalVisit();

            if (originalVisit != null) {
                originalVisit.setManagerVisit(null);
            }
        }

        managerVisitRepository.deleteAll(managerVisits);
    }



    @Transactional
    public ManagerVisitDto markVisit(MarkVisitRequestDto dto) {
        ManagerVisit visit = managerVisitRepository.findById(dto.getVisitId())
                .orElseThrow(() -> new EntityNotFoundException("Visit not found"));

        Doctor doctor = visit.getOriginalVisit().getDoctor();
//        visit.setActualDate(LocalDateTime.now());
        visit.setJoinedAt(LocalDateTime.now());

        //  Update visit fields
        visit.setStatus(dto.getStatus());
        visit.setManagerNotes(dto.getNotes());
        visit.setActivitiesPerformed(dto.getActivitiesPerformed());
        ManagerVisit savedVisit = managerVisitRepository.save(visit);
        return mapToDto(savedVisit);
    }

    @Transactional
    public ManagerVisitDto reMarkVisit(MarkVisitRequestDto dto) {

        ManagerVisit visit = managerVisitRepository.findById(dto.getVisitId())
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
                managerVisitRepository
                        .existsByManagerIdAndDoctorIdAndVisitDateAndStatusAndIdNot(
                                visit.getManager().getId(),
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
        ManagerVisit savedVisit = managerVisitRepository.save(visit);
        return mapToDto(savedVisit);
    }


    public ManagerVisitDto mapToDto(ManagerVisit visit) {
        return ManagerVisitDto.builder()
                .name(visit.getManager().getName())
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


    public List<TodayScheduledVisitDto> getTodaysVisits(Long feId) {

        ZoneId zone = ZoneId.of("Asia/Kolkata");
        LocalDate today = LocalDate.now(zone);

        LocalDateTime start = today.minusDays(2).atStartOfDay();   // Feb 17 00:00
        LocalDateTime end   = today.plusDays(1).atStartOfDay();    // Feb 20 00:00

        List<ManagerVisit> visits = managerVisitRepository.findTodaysVisitsByManager(
                start,
                end,
                feId
        );

        return visits.stream()
                .map(this::toTodayScheduledVisitDTO)
                .toList();
    }


    public List<TodayScheduledVisitDto> getTodaysVisitsScheduledOnly(Long managerId) {

        ZoneId zone = ZoneId.of("Asia/Kolkata");
        LocalDate today = LocalDate.now(zone);

        List<ManagerVisit> visits = managerVisitRepository.findTodayScheduledVisitsByManager(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay(),
                Visit.VisitStatus.SCHEDULED,
                managerId
        );

        return visits.stream()
                .map(this::toTodayScheduledVisitDTO)
                .toList();
    }

    private TodayScheduledVisitDto toTodayScheduledVisitDTO(ManagerVisit mv) {

        FieldExecutive fe = mv.getFieldExecutive();

        return new TodayScheduledVisitDto(
                mv.getId(),
                mv.getVisitType(),
                mv.getVisitDate(),
                String.valueOf(mv.getStatus()),

                // Doctor snapshot (no entity join)
                mv.getDoctorId(),
                mv.getDoctorName(),
                mv.getDoctorDesignation(), // designation not stored in ManagerVisit
                mv.getDoctorCategory() != null ? mv.getDoctorCategory().name() : null,
                null, // practiceType not stored
                mv.getHospitalName(),

                // Pharmacy not applicable in ManagerVisit snapshots
                null,
                null,
                null,
                null,

                fe.getId(),
                fe.getName()
        );
    }

    public List<CompletedVisitDto> getCompletedVisits(Long managerId) {

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        return managerVisitRepository
                .findAllCompletedManagerVisits(managerId, startOfMonth, endOfMonth)
                .stream()
                .map(this::mapToCompletedVisitDto)
                .toList();
    }

    public List<CompletedVisitDto> getMissedVisits(Long fieldExecutiveId) {

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        return managerVisitRepository
                .findAllMissedManagerVisits(fieldExecutiveId, startOfMonth, endOfMonth)
                .stream()
                .map(this::mapToCompletedVisitDto)
                .toList();
    }


    public List<CompletedVisitDto> getAllMissedVisits() {

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        return managerVisitRepository
                .findAllMissedManagerVisits(startOfMonth, endOfMonth)
                .stream()
                .map(this::mapToCompletedVisitDto)
                .toList();
    }


    private CompletedVisitDto mapToCompletedVisitDto(ManagerVisit v) {

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
                        .managerName(v.getManager().getName())
                        .managerEmpCode(v.getManager().getEmployeeCode())
                        .userRole("MANAGER")
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


    @Transactional
    public ManagerVisitDto createAndMarkUnscheduledVisit(
            CreateUnscheduledManagerVisitRequest dto
    ) {

        Manager manager = managerRepository.findById(dto.getManagerId())
                .orElseThrow(() -> new EntityNotFoundException("Manager not found"));

//        FieldExecutive fe = fieldExecutiveRepository.findById(dto.getFieldExecutiveId())
//                .orElseThrow(() -> new EntityNotFoundException("FE not found"));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        LocalDateTime now = LocalDateTime.now();

        // Rule: only ONE completed visit per doctor per day (manager)
        boolean alreadyCompleted =
                managerVisitRepository
                        .existsByManagerIdAndDoctorIdAndVisitDateAndStatus(
                                manager.getId(),
                                doctor.getId(),
                                now.toLocalDate(),
                                Visit.VisitStatus.COMPLETED
                        );

        if (alreadyCompleted) {
            throw new IllegalStateException(
                    "Doctor already has a completed visit for today"
            );
        }

        ManagerVisit visit = ManagerVisit.builder()
                .manager(manager)
//                .fieldExecutive(fe)

                // Unscheduled
                .unscheduled(true)
                .originalVisit(null)

                // Snapshot
                .visitDate(now.toLocalDate())
                .weekNumber(now.getDayOfYear() / 7 + 1) // optional
                .dayOfWeek(now.getDayOfWeek().getValue())
                .scheduledDate(now)

                .visitType(Visit.VisitType.DOCTOR)
                .status(Visit.VisitStatus.COMPLETED)

                .doctorId(doctor.getId())
                .doctorName(doctor.getName())
                .doctorCategory(doctor.getCategory())
                .hospitalName(doctor.getHospitalName())

                // Manager data
                .joinedAt(now)
                .managerNotes(dto.getNotes())
                .activitiesPerformed(dto.getActivitiesPerformed())

                .build();

        ManagerVisit saved = managerVisitRepository.save(visit);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public VisitComplianceResponse getManagerVisitCompliance(
            Long managerId,
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

        List<ManagerVisit> visits =
                managerVisitRepository.findManagerComplianceRecords(
                        managerId,
                        weekNumber,
                        startOfMonth,
                        endOfMonth
                );

        List<ComplianceRecordDto> records = visits.stream()
                .map(this::convertToManagerComplianceRecordDto)
                .toList();

        ComplianceStatsDto stats =
                calculateManagerComplianceStats(visits);

        int totalWeeks = calculateWeeksInMonth(now);

        return VisitComplianceResponse.builder()
                .stats(stats)
                .records(records)
                .totalWeeks(totalWeeks)
                .build();
    }


    private ComplianceStatsDto calculateManagerComplianceStats(
            List<ManagerVisit> visits
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


    private ComplianceRecordDto convertToManagerComplianceRecordDto(
            ManagerVisit visit
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


    private String getMissedReason(Visit visit) {
        if (visit.getStatus() == Visit.VisitStatus.MISSED) {
            if (visit.getNotes() != null && !visit.getNotes().isEmpty()) {
                return visit.getNotes();
            }
            return "Not specified";
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<CompletedVisitDto> getVisitsForManagerWeekAndDay(
            Long managerId,
            Integer weekNumber,
            Integer dayOfWeek
    ) {

        LocalDate now = getStartOfTheMonth();

        LocalDateTime startOfMonth = now.atStartOfDay();
        LocalDateTime endOfMonth =
                now.withDayOfMonth(now.lengthOfMonth()).atTime(LocalTime.MAX);

        return managerVisitRepository
                .findByManagerAndWeekAndDayForMonth(
                        managerId,
                        weekNumber,
                        dayOfWeek,
                        startOfMonth,
                        endOfMonth
                )
                .stream()
                .map(this::mapToCompletedVisitDto)
                .toList();
    }

    // Services for admin

    @Transactional
    public CompletedVisitDto markManagerVisitAsCompleted(Long managerVisitId) {

        ManagerVisit managerVisit = managerVisitRepository.findById(managerVisitId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ManagerVisit not found with id: " + managerVisitId
                ));

        if (managerVisit.getStatus() == Visit.VisitStatus.COMPLETED) {
            throw new IllegalStateException("Manager visit already completed");
        }

        if (managerVisit.getStatus() == Visit.VisitStatus.REJECTED) {
            throw new IllegalStateException("Rejected visit cannot be completed");
        }

        managerVisit.setStatus(Visit.VisitStatus.COMPLETED);
        managerVisit.setManagerNotes("Visit marked as completed by admin");
        ManagerVisit updatedVisit = managerVisitRepository.save(managerVisit);
        return mapToCompletedVisitDto(updatedVisit);
    }





}
