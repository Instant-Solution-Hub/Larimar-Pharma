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
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

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

        // Rule: Manager can't select multiple FEs for same day
        if (managerVisitRepository.existsByManagerIdAndWeekNumberAndDayOfWeek(
                request.getManagerId(),
                request.getWeekNumber(),
                request.getDayOfWeek()
        )) {
            throw new IllegalStateException(
                    "Manager already assigned for this week and day"
            );
        }

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

        for (Visit visit : visits) {

            // Safety: one manager visit per visit
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
                    .doctorCategory(visit.getDoctor().getCategory())
                    .hospitalName(visit.getDoctor().getHospitalName())

                    .build();

            managerVisitRepository.save(mv);
            visit.setManagerVisit(mv);
        }
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

        List<ManagerVisit> visits = managerVisitRepository.findTodaysVisitsByManager(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay(),
                feId
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
                null, // designation not stored in ManagerVisit
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


    private CompletedVisitDto mapToCompletedVisitDto(ManagerVisit v) {

        CompletedVisitDto.CompletedVisitDtoBuilder builder =
                CompletedVisitDto.builder()
                        .visitId(v.getId())
                        .visitType(v.getVisitType())
                        .visitDate(v.getVisitDate())
                        .weekNumber(v.getWeekNumber())
                        .dayOfWeek(v.getDayOfWeek())
                        .actualVisitTime(v.getJoinedAt())
                        .location("")
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




}
