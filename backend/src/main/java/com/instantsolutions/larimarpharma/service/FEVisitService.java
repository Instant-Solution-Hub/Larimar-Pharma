package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.MonthlyDoctorVisitDto;
import com.instantsolutions.larimarpharma.DTOs.MonthlyDoctorVisitStatDto;
import com.instantsolutions.larimarpharma.entity.Visit;
import com.instantsolutions.larimarpharma.entity.Visit.VisitStatus;
import com.instantsolutions.larimarpharma.entity.Visit.VisitType;
import com.instantsolutions.larimarpharma.exceptions.BadRequestException;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FEVisitService {

    @Autowired
    VisitRepository visitRepository;

    @Autowired
    FieldExecutiveRepository fieldExecutiveRepository;

    public MonthlyDoctorVisitStatDto getMonthlyDoctorVisitStats(
            Long feId, int year, int month) {

        // ✅ 1. Validate FE
        if (feId == null) {
            throw new BadRequestException("Field Executive ID cannot be null");
        }

        if (!fieldExecutiveRepository.existsById(feId)) {
            throw new ResourceNotFoundException(
                    "Field Executive not found with id: " + feId
            );
        }

        // ✅ 2. Validate month & year
        if (month < 1 || month > 12) {
            throw new BadRequestException("Month must be between 1 and 12");
        }

        if (year < 2000 || year > LocalDate.now().getYear()) {
            throw new BadRequestException("Invalid year: " + year);
        }

        // ✅ 3. Date range
        LocalDateTime start = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);

        // ✅ 4. Safe counts (null-safe)
        long total = safeCount(
                visitRepository.countByFieldExecutiveIdAndVisitTypeAndScheduledDateBetween(
                        feId, VisitType.DOCTOR, start, end));

        long scheduled = safeCount(
                visitRepository.countByFieldExecutiveIdAndVisitTypeAndStatusAndScheduledDateBetween(
                        feId, VisitType.DOCTOR, VisitStatus.SCHEDULED, start, end));

        long completed = safeCount(
                visitRepository.countByFieldExecutiveIdAndVisitTypeAndStatusAndScheduledDateBetween(
                        feId, VisitType.DOCTOR, VisitStatus.COMPLETED, start, end));

        long missed = safeCount(
                visitRepository.countByFieldExecutiveIdAndVisitTypeAndStatusAndScheduledDateBetween(
                        feId, VisitType.DOCTOR, VisitStatus.MISSED, start, end));

        return MonthlyDoctorVisitStatDto.builder()
                .fieldExecutiveId(feId)
                .year(year)
                .month(month)
                .totalDoctorVisits(total)
                .scheduledDoctorVisits(scheduled)
                .completedDoctorVisits(completed)
                .missedDoctorVisits(missed)
                .build();
    }
    private long safeCount(Long count) {
        return count != null ? count : 0L;
    }



    public List<MonthlyDoctorVisitDto> getCurrentMonthDoctorVisits(Long feId) {

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);

        List<Visit> visits = visitRepository.findDoctorVisitsForMonth(
                feId,
                VisitType.DOCTOR,
                start,
                end
        );

        return visits.stream()
                .map(visit -> {

                    // Validation for MISSED visit
                    if (visit.getStatus() == VisitStatus.MISSED &&
                            (visit.getNotes() == null || visit.getNotes().isBlank())) {
                        throw new BadRequestException(
                                "Reason is mandatory for missed visits. VisitId=" + visit.getId()
                        );
                    }

                    String doctorName = null;
                    String doctorCategory = null;

                    if (visit.getDoctor() != null) {
                        doctorName = visit.getDoctor().getName();
                        doctorCategory = visit.getDoctor().getCategory() != null
                                ? visit.getDoctor().getCategory().name()
                                : null;
                    }

                    return MonthlyDoctorVisitDto.builder()
                            .visitId(visit.getId())
                            .doctorName(doctorName)
                            .doctorCategory(doctorCategory)
                            .scheduledDate(visit.getScheduledDate())
                            .status(visit.getStatus() != null ? visit.getStatus().name() : null)
                            .reason(visit.getStatus() == VisitStatus.MISSED ? visit.getNotes() : null)
                            .build();
                })
                .collect(Collectors.toList());


    }
}
