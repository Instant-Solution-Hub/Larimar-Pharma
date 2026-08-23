package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.ManagerVisit;
import com.instantsolutions.larimarpharma.entity.Visit;
import com.instantsolutions.larimarpharma.entity.ZsmVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ZsmVisitRepository extends JpaRepository<ZsmVisit, Long>, JpaSpecificationExecutor<ZsmVisit> {
    List<ZsmVisit> findByZsmAdminIdAndVisitDate(
            Long zsmId,
            LocalDate visitDate
    );

    List<ZsmVisit> findByFieldExecutiveIdAndVisitDate(
            Long fieldExecutiveId,
            LocalDate visitDate
    );

    boolean existsByZsmAdminIdAndDoctorIdAndVisitDateAndStatusAndIdNot(
            Long zsmId,
            Long doctorId,
            LocalDate visitDate,
            Visit.VisitStatus status,
            Long id
    );

    @Query("""
    SELECT DISTINCT zv
    FROM ZsmVisit zv
    LEFT JOIN FETCH zv.fieldExecutive fe
    LEFT JOIN FETCH zv.originalVisit ov
    WHERE zv.scheduledDate >= :start
      AND zv.scheduledDate < :nextDay
      AND zv.status IN (
            com.instantsolutions.larimarpharma.entity.Visit$VisitStatus.SCHEDULED,
            com.instantsolutions.larimarpharma.entity.Visit$VisitStatus.MISSED
      )
      AND zv.zsmAdmin.id = :zsmId
""")
    List<ZsmVisit> findTodaysVisitsByZsm(
            @Param("start") LocalDateTime start,
            @Param("nextDay") LocalDateTime nextDay,
            @Param("zsmId") Long zsmId
    );


    @Query("""
    SELECT DISTINCT zv
    FROM ZsmVisit zv
    LEFT JOIN FETCH zv.fieldExecutive fe
    LEFT JOIN FETCH zv.originalVisit ov
    WHERE zv.zsmAdmin.id = :zsmId
      AND (
            (
                zv.status = com.instantsolutions.larimarpharma.entity.Visit$VisitStatus.SCHEDULED
                AND zv.scheduledDate >= :start
                AND zv.scheduledDate < :nextDay
            )
            OR
            (
                zv.status = com.instantsolutions.larimarpharma.entity.Visit$VisitStatus.MISSED
                AND zv.scheduledDate >= :monthStart
                AND zv.scheduledDate < :nextDay
            )
      )
""")
    List<ZsmVisit> findTodaysAndMissedVisitsByZsm(
            @Param("start") LocalDateTime start,
            @Param("nextDay") LocalDateTime nextDay,
            @Param("monthStart") LocalDateTime monthStart,
            @Param("zsmId") Long zsmId
    );

    @Query("""
    SELECT DISTINCT zv
    FROM ZsmVisit zv
    LEFT JOIN FETCH zv.zsmAdmin za
    LEFT JOIN FETCH zv.fieldExecutive fe
    LEFT JOIN FETCH zv.originalVisit ov
    WHERE zv.scheduledDate >= :start
      AND zv.scheduledDate < :nextDay
      AND zv.status = :status
      AND za.id = :zsmId
""")
    List<ZsmVisit> findTodayScheduledVisitsByZsm(
            @Param("start") LocalDateTime start,
            @Param("nextDay") LocalDateTime nextDay,
            @Param("status") Visit.VisitStatus status,
            @Param("zsmId") Long zsmId
    );

    @Query("""
    SELECT zv
    FROM ZsmVisit zv
    WHERE zv.zsmAdmin.id = :zsmId
      AND zv.status = com.instantsolutions.larimarpharma.entity.Visit$VisitStatus.COMPLETED
      AND zv.visitDate BETWEEN :startDate AND :endDate
    ORDER BY zv.scheduledDate DESC
""")
    List<ZsmVisit> findAllCompletedZsmVisits(
            @Param("zsmId") Long zsmId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT zv
    FROM ZsmVisit zv
    WHERE zv.zsmAdmin.id = :zsmId
      AND zv.status = com.instantsolutions.larimarpharma.entity.Visit$VisitStatus.MISSED
      AND zv.visitDate BETWEEN :startDate AND :endDate
    ORDER BY zv.scheduledDate DESC
""")
    List<ZsmVisit> findAllMissedZsmVisits(
            @Param("zsmId") Long zsmId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT zv
    FROM ZsmVisit zv
    WHERE zv.status = com.instantsolutions.larimarpharma.entity.Visit$VisitStatus.MISSED
      AND zv.visitDate BETWEEN :startDate AND :endDate
    ORDER BY zv.scheduledDate DESC
""")
    List<ZsmVisit> findAllMissedZsmVisits(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT zv
    FROM ZsmVisit zv
    WHERE zv.zsmAdmin.id = :zsmId
      AND zv.scheduledDate BETWEEN :startDate AND :endDate
      AND (:weekNumber IS NULL OR zv.weekNumber = :weekNumber)
      AND zv.visitType = 'DOCTOR'
    ORDER BY zv.scheduledDate
""")
    List<ZsmVisit> findZsmComplianceRecords(
            @Param("zsmId") Long zsmId,
            @Param("weekNumber") Integer weekNumber,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Query("""
    SELECT zv
    FROM ZsmVisit zv
    WHERE zv.zsmAdmin.id = :zsmId
      AND zv.visitDate = :selectedDate
    ORDER BY zv.visitDate ASC
""")
    List<ZsmVisit> findByZsmAndWeekAndDayForMonth(
            @Param("zsmId") Long zsmId,
            @Param("selectedDate") LocalDate selectedDate
    );


    @Query("""
    SELECT zv
    FROM ZsmVisit zv
    WHERE zv.zsmAdmin.id = :zsmId
      AND zv.visitDate = :selectedDate
    ORDER BY zv.visitDate ASC
""")
    List<ZsmVisit> findByZsmAndDate(
            @Param("zsmId") Long zsmId,
            @Param("selectedDate") LocalDate selectedDate
    );



}
