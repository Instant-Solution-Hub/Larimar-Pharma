package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.ManagerVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ManagerVisitRepository extends JpaRepository<ManagerVisit, Long> {

    boolean existsByManagerIdAndOriginalVisitId(
            Long managerId,
            Long originalVisitId
    );

    boolean existsByManagerIdAndWeekNumberAndDayOfWeek(
            Long managerId,
            Integer weekNumber,
            Integer dayOfWeek
    );

    int countByManagerIdAndScheduledDateBetween(Long managerId,
                                                LocalDateTime startDate,
                                                LocalDateTime endDate);

    int countByFieldExecutiveIdAndScheduledDateBetween(Long fieldExecutiveId,
                                                       LocalDateTime startDate,
                                                       LocalDateTime endDate);

    List<ManagerVisit> findByManagerIdAndScheduledDateBetweenOrderByScheduledDateAsc(
            Long managerId, LocalDateTime startDate, LocalDateTime endDate);

    List<ManagerVisit> findByFieldExecutiveId(Long fieldExecutiveId);
}
