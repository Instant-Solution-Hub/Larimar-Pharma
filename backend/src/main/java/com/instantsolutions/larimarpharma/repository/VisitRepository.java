package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.DTOs.TodayScheduledVisitDto;
import com.instantsolutions.larimarpharma.DTOs.VisitCountProjection;
import com.instantsolutions.larimarpharma.entity.Visit;
import com.instantsolutions.larimarpharma.entity.Visit.VisitStatus;
import com.instantsolutions.larimarpharma.entity.Visit.VisitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {


    long countByFieldExecutiveIdAndVisitTypeAndStatusAndScheduledDateBetween(
            Long feId,
            VisitType visitType,
            VisitStatus status,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
        SELECT v FROM Visit v
        JOIN FETCH v.doctor d
        WHERE v.fieldExecutive.id = :feId
          AND v.visitType = :visitType
          AND v.scheduledDate BETWEEN :startDate AND :endDate
        ORDER BY v.scheduledDate DESC
    """)
    List<Visit> findDoctorVisitsForMonth(
            @Param("feId") Long feId,
            @Param("visitType") VisitType visitType,
            @Param("startDate") LocalDateTime start,
            @Param("endDate") LocalDateTime end);




    @Query("""
        SELECT
            COUNT(CASE WHEN v.visitType = 'DOCTOR' THEN 1 END) AS doctor,
            COUNT(CASE WHEN v.visitType = 'PHARMACIST' THEN 1 END) AS pharmacist,
            COUNT(CASE WHEN v.visitType = 'STOCKIST' THEN 1 END) AS stockist
        FROM Visit v
        WHERE v.fieldExecutive.id = :feId
          AND v.status = com.instantsolutions.larimarpharma.entity.Visit.VisitStatus.COMPLETED
          AND v.actualDate BETWEEN :startDate AND :endDate
    """)
    VisitCountProjection getCompletedVisitCountsForMonth(
            @Param("feId") Long feId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );



    List<Visit> findByVisitTypeAndStatusAndScheduledDateBefore(
            VisitType visitType,
            VisitStatus status,
            LocalDateTime now
    );


    // Total doctor visits (including scheduled + completed)
    long countByFieldExecutiveIdAndVisitTypeAndScheduledDateBetween(
            Long feId,
            Visit.VisitType visitType,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    boolean existsByFieldExecutiveIdAndDoctorIdAndVisitDate(
            Long fieldExecutiveId,
            Long doctorId,
            LocalDate visitDate
    );

    List<Visit> findByFieldExecutiveIdAndVisitDateBetween(
            Long fieldExecutiveId,
            LocalDate start,
            LocalDate end
    );

    @Query("""
        SELECT v
        FROM Visit v
        JOIN FETCH v.doctor d
        WHERE v.fieldExecutive.id = :feId
          AND v.weekNumber = :weekNumber
          AND v.dayOfWeek = :dayOfWeek
          AND v.visitDate BETWEEN :startDate AND :endDate
    """)
    List<Visit> findVisitsForSlot(
            @Param("feId") Long fieldExecutiveId,
            @Param("weekNumber") Integer weekNumber,
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT v.doctor.id, COUNT(v)
    FROM Visit v
    WHERE v.fieldExecutive.id = :feId
      AND v.status = 'COMPLETED'
      AND v.visitDate BETWEEN :startDate AND :endDate
    GROUP BY v.doctor.id
""")
    List<Object[]> countCompletedVisitsPerDoctor(
            @Param("feId") Long fieldExecutiveId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT v.doctor.id, COUNT(v)
    FROM Visit v
    WHERE v.fieldExecutive.id = :feId
      AND v.visitDate BETWEEN :startDate AND :endDate
    GROUP BY v.doctor.id
""")
    List<Object[]> countPlannedVisitsPerDoctor(
            @Param("feId") Long fieldExecutiveId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query("""
    SELECT v
    FROM Visit v
    JOIN FETCH v.pharmacy p
    WHERE v.fieldExecutive.id = :feId
      AND v.weekNumber = :weekNumber
      AND v.dayOfWeek = :dayOfWeek
      AND v.visitType = 'PHARMACIST'
      AND v.visitDate BETWEEN :startDate AND :endDate
""")
    List<Visit> findPharmacyVisitsForSlot(
            @Param("feId") Long fieldExecutiveId,
            @Param("weekNumber") Integer weekNumber,
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT v.pharmacy.id, COUNT(v)
    FROM Visit v
    WHERE v.fieldExecutive.id = :feId
      AND v.visitType = 'PHARMACIST'
      AND v.status = 'COMPLETED'
      AND v.visitDate BETWEEN :startDate AND :endDate
    GROUP BY v.pharmacy.id
""")
    List<Object[]> countCompletedVisitsPerPharmacy(
            @Param("feId") Long fieldExecutiveId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT v.pharmacy.id, COUNT(v)
    FROM Visit v
    WHERE v.fieldExecutive.id = :feId
      AND v.visitType = 'PHARMACIST'
      AND v.visitDate BETWEEN :startDate AND :endDate
    GROUP BY v.pharmacy.id
""")
    List<Object[]> countPlannedVisitsPerPharmacy(
            @Param("feId") Long fieldExecutiveId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT v
    FROM Visit v
    LEFT JOIN FETCH v.doctor d
    LEFT JOIN FETCH v.pharmacy p
    LEFT JOIN FETCH v.stockist s
    WHERE v.fieldExecutive.id = :feId
      AND v.status = 'COMPLETED'
      AND v.visitDate BETWEEN :startDate AND :endDate
    ORDER BY v.actualVisitTime DESC
""")
    List<Visit> findAllCompletedVisits(
            @Param("feId") Long fieldExecutiveId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query("""
    SELECT v
    FROM Visit v
    JOIN FETCH v.doctor d
    WHERE v.fieldExecutive.id = :feId
      AND v.visitType = 'DOCTOR'
      AND v.visitDate = :today
      AND v.status IN ('SCHEDULED', 'APPROVED')
""")
    List<Visit> findTodayScheduledDoctorVisits(
            @Param("feId") Long fieldExecutiveId,
            @Param("today") LocalDate today
    );


    @Query("""
    SELECT v
    FROM Visit v
    JOIN FETCH v.pharmacy p
    WHERE v.fieldExecutive.id = :feId
      AND v.visitType = 'PHARMACIST'
      AND v.visitDate = :today
      AND v.status IN ('SCHEDULED', 'APPROVED')
""")
    List<Visit> findTodayScheduledPharmacyVisits(
            @Param("feId") Long fieldExecutiveId,
            @Param("today") LocalDate today
    );



    @Query("""
    SELECT DISTINCT v
    FROM Visit v
    LEFT JOIN FETCH v.doctor
    LEFT JOIN FETCH v.pharmacy
    LEFT JOIN FETCH v.fieldExecutive fe
    WHERE v.scheduledDate >= :start
      AND v.scheduledDate < :nextDay
      AND v.status = :status
      AND fe.id = :fieldExecutiveId
""")
    List<Visit> findTodayScheduledVisitsByFieldExecutive(
            @Param("start") LocalDateTime start,
            @Param("nextDay") LocalDateTime nextDay,
            @Param("status") Visit.VisitStatus status,
            @Param("fieldExecutiveId") Long fieldExecutiveId
    );


}

