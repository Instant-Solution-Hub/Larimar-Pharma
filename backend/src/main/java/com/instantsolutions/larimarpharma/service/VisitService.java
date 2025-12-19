package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.VisitDashboardResponse;
import com.instantsolutions.larimarpharma.entity.Visit;
import com.instantsolutions.larimarpharma.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;

    public VisitDashboardResponse getDashboard(Long fieldExecutiveId) {

        LocalDate now = LocalDate.now();

        LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = now.withDayOfMonth(now.lengthOfMonth())
                .atTime(LocalTime.MAX);

        Object[] counts = visitRepository.getCompletedVisitCountsForMonth(
                fieldExecutiveId,
                startOfMonth,
                endOfMonth
        );

        long doctorCompleted = (long) counts[0];
        long pharmacistCompleted = (long) counts[1];
        long stockistCompleted = (long) counts[2];

        long totalDoctorVisitsForMonth =
                visitRepository.countByFieldExecutiveIdAndVisitTypeAndScheduledDateBetween(
                        fieldExecutiveId,
                        Visit.VisitType.DOCTOR,
                        startOfMonth,
                        endOfMonth
                );

        String doctorTargetProgress = calculateProgress(
                doctorCompleted,
                totalDoctorVisitsForMonth
        );

        return VisitDashboardResponse.builder()
                .doctorVisits(doctorCompleted)
                .pharmacyVisits(pharmacistCompleted)
                .stockistVisit(stockistCompleted)
                .totalDoctorVisitsForTheCurrentMonth(totalDoctorVisitsForMonth)
                .doctorTargetProgress(doctorTargetProgress)
                .build();
    }

    private String calculateProgress(long completed, long target) {
        if (target == 0) return "0%";
        long percentage = Math.round((completed * 100.0) / target);
        return percentage + "%";
    }
}
