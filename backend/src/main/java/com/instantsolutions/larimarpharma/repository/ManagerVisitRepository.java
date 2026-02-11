package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.entity.ManagerVisit;
import com.instantsolutions.larimarpharma.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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
          AND mv.weekNumber = :weekNumber
          AND mv.dayOfWeek = :dayOfWeek
          AND mv.scheduledDate BETWEEN :startOfMonth AND :endOfMonth
        ORDER BY mv.scheduledDate ASC
    """)
    List<ManagerVisit> findByManagerAndWeekAndDayForMonth(
            @Param("managerId") Long managerId,
            @Param("weekNumber") Integer weekNumber,
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("endOfMonth") LocalDateTime endOfMonth
    );


    // Manager methods
    @Query("""
    SELECT CASE WHEN COUNT(mv) > 0 THEN true ELSE false END
    FROM ManagerVisit mv
    WHERE mv.manager = :manager
      AND mv.scheduledDate >= :start
      AND mv.scheduledDate < :end
      AND mv.status IN ('SCHEDULED', 'APPROVED')
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



}
