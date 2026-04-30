package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.DTOs.ManagerVisitSummaryProjection;
import com.instantsolutions.larimarpharma.DTOs.ManagerVisitSummaryResponseDto;
import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.entity.ManagerVisit;
import com.instantsolutions.larimarpharma.entity.Visit;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ManagerVisitRepository extends JpaRepository<ManagerVisit, Long>, JpaSpecificationExecutor<ManagerVisit> {

    boolean existsByManagerIdAndOriginalVisitId(
            Long managerId,
            Long originalVisitId
    );

    List<ManagerVisit> findByManagerIdAndWeekNumberAndDayOfWeek(
            Long managerId,
            Integer weekNumber,
            Integer dayOfWeek
    );

    List<ManagerVisit> findByManagerIdAndVisitDate(
            Long managerId,
            LocalDate visitDate
    );

    void deleteByManagerIdAndWeekNumberAndDayOfWeek(
            Long managerId,
            Integer weekNumber,
            Integer dayOfWeek
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

    boolean existsByDoctorIdAndVisitDateAndStatus(
            Long doctorId,
            LocalDate visitDate,
            Visit.VisitStatus status
    );

    boolean existsByManagerIdAndDoctorIdAndVisitDateAndStatusAndIdNot(
            Long managerId,
            Long doctorId,
            LocalDate visitDate,
            Visit.VisitStatus status,
            Long id
    );

    @Query("""
    SELECT DISTINCT mv
    FROM ManagerVisit mv
    LEFT JOIN FETCH mv.manager m
    LEFT JOIN FETCH mv.fieldExecutive fe
    LEFT JOIN FETCH mv.originalVisit ov
    WHERE mv.scheduledDate >= :start
      AND mv.scheduledDate < :nextDay
      AND mv.status = :status
      AND m.id = :managerId
""")
    List<ManagerVisit> findTodayScheduledVisitsByManager(
            @Param("start") LocalDateTime start,
            @Param("nextDay") LocalDateTime nextDay,
            @Param("status") Visit.VisitStatus status,
            @Param("managerId") Long managerId
    );


    @Query("""
    SELECT mv
    FROM ManagerVisit mv
    WHERE mv.fieldExecutive.id = :feId
      AND mv.visitType = com.instantsolutions.larimarpharma.entity.Visit$VisitType.DOCTOR
      AND mv.visitDate = :today
      AND mv.status IN (
            com.instantsolutions.larimarpharma.entity.Visit$VisitStatus.SCHEDULED,
            com.instantsolutions.larimarpharma.entity.Visit$VisitStatus.APPROVED
      )
""")
    List<ManagerVisit> findTodayScheduledDoctorVisitsByManager(
            @Param("feId") Long fieldExecutiveId,
            @Param("today") LocalDate today
    );


    @Query("""
    SELECT DISTINCT mv
    FROM ManagerVisit mv
    LEFT JOIN FETCH mv.fieldExecutive fe
    LEFT JOIN FETCH mv.originalVisit ov
    WHERE mv.scheduledDate >= :start
      AND mv.scheduledDate < :nextDay
      AND mv.status IN (
            com.instantsolutions.larimarpharma.entity.Visit$VisitStatus.SCHEDULED,
            com.instantsolutions.larimarpharma.entity.Visit$VisitStatus.MISSED
      )
      AND mv.manager.id = :managerId
""")
    List<ManagerVisit> findTodaysVisitsByManager(
            @Param("start") LocalDateTime start,
            @Param("nextDay") LocalDateTime nextDay,
            @Param("managerId") Long managerId
    );


    @Query("""
SELECT DISTINCT mv
FROM ManagerVisit mv
LEFT JOIN FETCH mv.fieldExecutive fe
LEFT JOIN FETCH mv.originalVisit ov
WHERE mv.manager.id = :managerId
AND (
      (mv.status = com.instantsolutions.larimarpharma.entity.Visit$VisitStatus.SCHEDULED
        AND mv.scheduledDate >= :start
        AND mv.scheduledDate < :nextDay)
   OR (mv.status = com.instantsolutions.larimarpharma.entity.Visit$VisitStatus.MISSED
        AND mv.scheduledDate >= :monthStart
        AND mv.scheduledDate < :nextDay)
)
""")
    List<ManagerVisit> findTodaysAndMissedVisitsByManager(
            @Param("start") LocalDateTime start,
            @Param("nextDay") LocalDateTime nextDay,
            @Param("monthStart") LocalDateTime monthStart,
            @Param("managerId") Long managerId
    );


    @Query("""
    SELECT mv
    FROM ManagerVisit mv
    WHERE mv.manager.id = :managerId
      AND mv.status = com.instantsolutions.larimarpharma.entity.Visit$VisitStatus.COMPLETED
      AND mv.visitDate BETWEEN :startDate AND :endDate
    ORDER BY mv.scheduledDate DESC
""")
    List<ManagerVisit> findAllCompletedManagerVisits(
            @Param("managerId") Long managerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query("""
    SELECT mv
    FROM ManagerVisit mv
    WHERE mv.manager.id = :managerId
      AND mv.status = com.instantsolutions.larimarpharma.entity.Visit$VisitStatus.MISSED
      AND mv.visitDate BETWEEN :startDate AND :endDate
    ORDER BY mv.scheduledDate DESC
""")
    List<ManagerVisit> findAllMissedManagerVisits(
            @Param("managerId") Long managerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT mv
    FROM ManagerVisit mv
    WHERE mv.status = com.instantsolutions.larimarpharma.entity.Visit$VisitStatus.MISSED
      AND mv.visitDate BETWEEN :startDate AND :endDate
    ORDER BY mv.scheduledDate DESC
""")
    List<ManagerVisit> findAllMissedManagerVisits(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );



    boolean existsByManagerIdAndDoctorIdAndVisitDateAndStatus(
            Long managerId,
            Long doctorId,
            LocalDate visitDate,
            Visit.VisitStatus status
    );


    @Query("""
    SELECT mv
    FROM ManagerVisit mv
    WHERE mv.manager.id = :managerId
      AND mv.scheduledDate BETWEEN :startDate AND :endDate
      AND (:weekNumber IS NULL OR mv.weekNumber = :weekNumber)
      AND mv.visitType = 'DOCTOR'
    ORDER BY mv.scheduledDate
""")
    List<ManagerVisit> findManagerComplianceRecords(
            @Param("managerId") Long managerId,
            @Param("weekNumber") Integer weekNumber,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Query("""
        SELECT mv
        FROM ManagerVisit mv
        WHERE mv.manager.id = :managerId
          AND mv.visitDate =:selectedDate
        ORDER BY mv.visitDate ASC
    """)
    List<ManagerVisit> findByManagerAndWeekAndDayForMonth(
            @Param("managerId") Long managerId,
            @Param("selectedDate") LocalDate selectedDate
    );

    @Query("""
        SELECT mv
        FROM ManagerVisit mv
        WHERE mv.manager.id = :managerId
          AND mv.visitDate =:selectedDate
        ORDER BY mv.visitDate ASC
    """)
    List<ManagerVisit> findByManagerAndDate(
            @Param("managerId") Long managerId,
            @Param("selectedDate") LocalDate selectedDate
    );


    // Manager methods
    @Query("""
    SELECT CASE WHEN COUNT(mv) > 0 THEN true ELSE false END
    FROM ManagerVisit mv
    WHERE mv.manager = :manager
      AND mv.scheduledDate >= :start
      AND mv.scheduledDate < :end
      AND mv.status IN ('SCHEDULED')
""")
    boolean existsScheduledManagerVisits(
            @Param("manager") Manager manager,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    @Query("""
    SELECT CASE WHEN COUNT(mv) > 0 THEN true ELSE false END
    FROM ManagerVisit mv
    WHERE mv.manager = :manager
      AND mv.joinedAt >= :start
      AND mv.joinedAt < :end
      AND mv.status IN ('COMPLETED', 'MISSED')
""")
    boolean existsCompletedManagerVisits(
            @Param("manager") Manager manager,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<ManagerVisit> findByVisitDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT mv.visitDate, COUNT(mv) FROM ManagerVisit mv " +
            "WHERE mv.visitDate BETWEEN :startDate AND :endDate " +
            "AND mv.status = :status " +
            "GROUP BY mv.visitDate")
    List<Object[]> countByDateAndStatus(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") Visit.VisitStatus status);


    List<ManagerVisit> findByFieldExecutiveIdAndWeekNumberAndDayOfWeek(
            Long fieldExecutiveId,
            Integer weekNumber,
            Integer dayOfWeek
    );

    List<ManagerVisit> findByFieldExecutiveIdAndVisitDate(
            Long fieldExecutiveId,
            LocalDate visitDate
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    UPDATE ManagerVisit mv
    SET mv.status = com.instantsolutions.larimarpharma.entity.Visit.VisitStatus.MISSED,
        mv.managerNotes = :note
    WHERE mv.manager.id = :managerId
    AND mv.scheduledDate BETWEEN :fromDate AND :toDate
    AND mv.status IN (
        com.instantsolutions.larimarpharma.entity.Visit.VisitStatus.SCHEDULED,
        com.instantsolutions.larimarpharma.entity.Visit.VisitStatus.APPROVED
    )
""")
    int markManagerVisitsAsMissedForLeave(
            @Param("managerId") Long managerId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("note") String note
    );


    @Query("""
    SELECT
        COALESCE(SUM(CASE WHEN mv.status = 'COMPLETED' THEN 1 ELSE 0 END),0) as completedVisitCount,
        COALESCE(SUM(CASE WHEN mv.status = 'MISSED' THEN 1 ELSE 0 END),0) as missedVisitCount,
        COALESCE(SUM(CASE WHEN mv.status = 'COMPLETED' AND mv.visitType = 'DOCTOR' THEN 1 ELSE 0 END),0) as completedDoctorVisitCount,
        COALESCE(SUM(CASE WHEN mv.status = 'MISSED' AND mv.visitType = 'DOCTOR' THEN 1 ELSE 0 END),0) as missedDoctorVisitCount,
        COALESCE(SUM(CASE WHEN mv.status = 'COMPLETED' AND mv.visitType = 'DOCTOR' AND mv.doctorCategory = 'A_PLUS' THEN 1 ELSE 0 END),0) as completedAPlusVisits,
        COALESCE(SUM(CASE WHEN mv.status = 'MISSED' AND mv.visitType = 'DOCTOR' AND mv.doctorCategory = 'A_PLUS' THEN 1 ELSE 0 END),0) as missedAPlusVisits,
        COALESCE(SUM(CASE WHEN mv.status = 'COMPLETED' AND mv.visitType = 'DOCTOR' AND mv.doctorCategory = 'A' THEN 1 ELSE 0 END),0) as completedAVisits,
        COALESCE(SUM(CASE WHEN mv.status = 'MISSED' AND mv.visitType = 'DOCTOR' AND mv.doctorCategory = 'A' THEN 1 ELSE 0 END),0) as missedAVisits,
        COALESCE(SUM(CASE WHEN mv.status = 'COMPLETED' AND mv.visitType = 'DOCTOR' AND mv.doctorCategory = 'B' THEN 1 ELSE 0 END),0) as completedBVisits,
        COALESCE(SUM(CASE WHEN mv.status = 'MISSED' AND mv.visitType = 'DOCTOR' AND mv.doctorCategory = 'B' THEN 1 ELSE 0 END),0) as missedBVisits
    FROM ManagerVisit mv
    WHERE mv.manager.id = :managerId
    AND mv.scheduledDate BETWEEN :fromDate AND :toDate
    """)
    Optional<ManagerVisitSummaryProjection> getManagerVisitSummary(
            @Param("managerId") Long managerId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
