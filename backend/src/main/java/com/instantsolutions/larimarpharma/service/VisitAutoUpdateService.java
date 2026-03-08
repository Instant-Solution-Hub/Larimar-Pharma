package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.entity.Visit;
import com.instantsolutions.larimarpharma.entity.Visit.VisitStatus;
import com.instantsolutions.larimarpharma.entity.Visit.VisitType;
import com.instantsolutions.larimarpharma.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisitAutoUpdateService {

    private final VisitRepository visitRepository;

    /**
     * Runs every day at 12:05 AM
     */
    @Scheduled(cron = "0 5 0 * * ?", zone = "Asia/Kolkata")
    @Transactional
    public void markOverdueScheduledVisitsAsMissed() {

        LocalDate today = LocalDate.now();

        List<Visit> overdueVisits =
                visitRepository.findByVisitTypeAndStatusAndVisitDateBefore(
                        VisitType.DOCTOR,
                        VisitStatus.SCHEDULED,
                        today
                );

        for (Visit visit : overdueVisits) {
            visit.setStatus(VisitStatus.MISSED);
            visit.setNotes("USER DID NOT UPDATE THE STATUS");
            visit.setActualDate(visit.getVisitDate().atStartOfDay());
        }

        log.info("Auto-marked {} visits as MISSED", overdueVisits.size());
    }
}
