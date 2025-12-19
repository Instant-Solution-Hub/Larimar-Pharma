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
    @Scheduled(cron = "0 5 0 * * ?")
    @Transactional
    public void markOverdueScheduledVisitsAsMissed() {

        LocalDateTime now = LocalDateTime.now();

        List<Visit> overdueVisits =
                visitRepository.findByVisitTypeAndStatusAndScheduledDateBefore(
                        VisitType.DOCTOR,
                        VisitStatus.SCHEDULED,
                        now
                );

        for (Visit visit : overdueVisits) {
            visit.setStatus(VisitStatus.MISSED);
            visit.setNotes("OWNER DID NOT UPDATE THE STATUS");
            visit.setActualDate(visit.getScheduledDate());
        }

        log.info("Auto-marked {} visits as MISSED", overdueVisits.size());
    }
}
