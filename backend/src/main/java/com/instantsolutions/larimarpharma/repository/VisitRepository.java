package com.instantsolutions.larimarpharma.repository;
import com.instantsolutions.larimarpharma.DTOs.ComplianceStatsProjection;
import com.instantsolutions.larimarpharma.DTOs.TodayScheduledVisitDto;
import com.instantsolutions.larimarpharma.DTOs.VisitCountProjection;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.Manager;
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

    boolean existsByDoctorIdAndVisitDateAndStatus(
            Long doctorId,
            LocalDate visitDate,
            Visit.VisitStatus status
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
    LEFT JOIN FETCH v.doctor d
    LEFT JOIN FETCH v.pharmacy p
    LEFT JOIN FETCH v.stockist s
    WHERE v.fieldExecutive.id = :feId
      AND v.status = 'MISSED'
      AND v.visitDate BETWEEN :startDate AND :endDate
    ORDER BY v.actualVisitTime DESC
""")
    List<Visit> findAllMissedVisits(
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
    WHERE v.status = 'MISSED'
      AND v.visitDate BETWEEN :startDate AND :endDate
    ORDER BY v.actualVisitTime DESC
""")
    List<Visit> findAllMissedVisits(
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

    @Query("""
    SELECT DISTINCT v
    FROM Visit v
    LEFT JOIN FETCH v.doctor
    LEFT JOIN FETCH v.pharmacy
    LEFT JOIN FETCH v.fieldExecutive fe
    WHERE v.scheduledDate >= :start
      AND v.scheduledDate < :nextDay
      AND v.status IN ('SCHEDULED', 'MISSED')
      AND fe.id = :fieldExecutiveId
""")
    List<Visit> findTodaysVisitsByFieldExecutive(
            @Param("start") LocalDateTime start,
            @Param("nextDay") LocalDateTime nextDay,
            @Param("fieldExecutiveId") Long fieldExecutiveId
    );


    @Query("""
        SELECT 
            COUNT(v) as total,
            SUM(CASE WHEN v.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed,
            SUM(CASE WHEN v.status = 'MISSED' THEN 1 ELSE 0 END) as missed,
            SUM(CASE WHEN v.visitType = 'DOCTOR' AND v.status = 'COMPLETED' THEN 1 ELSE 0 END) as doctorCompleted,
            SUM(CASE WHEN v.visitType = 'PHARMACIST' AND v.status = 'COMPLETED' THEN 1 ELSE 0 END) as pharmacistCompleted
        FROM Visit v
        WHERE v.fieldExecutive.id = :fieldExecutiveId
        AND v.scheduledDate BETWEEN :startDate AND :endDate
        AND (:weekNumber IS NULL OR v.weekNumber = :weekNumber)
        AND v.visitType IN ('DOCTOR', 'PHARMACIST')
    """)
    ComplianceStatsProjection getComplianceStats(
            @Param("fieldExecutiveId") Long fieldExecutiveId,
            @Param("weekNumber") Integer weekNumber,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
        SELECT v
        FROM Visit v
        WHERE v.fieldExecutive.id = :fieldExecutiveId
        AND v.scheduledDate BETWEEN :startDate AND :endDate
        AND (:weekNumber IS NULL OR v.weekNumber = :weekNumber)
        AND v.visitType IN ('DOCTOR', 'PHARMACIST')
        ORDER BY v.scheduledDate
    """)
    List<Visit> findComplianceRecords(
            @Param("fieldExecutiveId") Long fieldExecutiveId,
            @Param("weekNumber") Integer weekNumber,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    boolean existsByDoctorIdAndVisitDateAndVisitType(
            Long doctorId,
            LocalDate visitDate,
            Visit.VisitType visitType
    );

    boolean existsByPharmacyIdAndVisitDateAndVisitType(
            Long pharmacyId,
            LocalDate visitDate,
            Visit.VisitType visitType
    );

    @Query("""
    SELECT d.category, COUNT(v)
    FROM Visit v
    JOIN v.doctor d
    WHERE v.fieldExecutive.id = :feId
      AND v.visitType = 'DOCTOR'
      AND v.status = 'COMPLETED'
      AND v.actualDate BETWEEN :start AND :end
    GROUP BY d.category
""")
    List<Object[]> countCompletedVisitsByCategory(
            Long feId,
            LocalDateTime start,
            LocalDateTime end
    );


    @Query("""
    SELECT d.category, COUNT(DISTINCT d.id)
    FROM Visit v
    JOIN v.doctor d
    WHERE v.fieldExecutive.id = :feId
      AND v.visitType = 'DOCTOR'
      AND v.status = 'COMPLETED'
      AND v.actualDate BETWEEN :start AND :end
    GROUP BY d.category
""")
    List<Object[]> countDistinctDoctorsVisitedByCategory(
            Long feId,
            LocalDateTime start,
            LocalDateTime end
    );


    @Query("""
    SELECT v
    FROM Visit v
    JOIN FETCH v.fieldExecutive fe
    JOIN FETCH v.doctor
    WHERE fe.manager.id = :managerId
      AND v.weekNumber = :weekNumber
      AND v.dayOfWeek = :dayOfWeek
      AND v.status = com.instantsolutions.larimarpharma.entity.Visit.VisitStatus.SCHEDULED
      AND v.visitType = com.instantsolutions.larimarpharma.entity.Visit.VisitType.DOCTOR
""")
    List<Visit> findScheduledDoctorVisitsForManagerByWeekAndDay(
            @Param("managerId") Long managerId,
            @Param("weekNumber") Integer weekNumber,
            @Param("dayOfWeek") Integer dayOfWeek
    );


    @Query("""
    SELECT v
    FROM Visit v
    JOIN v.doctor d
    WHERE v.fieldExecutive.id = :feId
      AND v.weekNumber = :weekNumber
      AND v.dayOfWeek = :dayOfWeek
      AND v.status = 'SCHEDULED'
      AND v.visitType = 'DOCTOR'
      AND d.category IN ('A_PLUS', 'A')
""")
    List<Visit> findEligibleManagerVisits(
            Long feId,
            Integer weekNumber,
            Integer dayOfWeek
    );

    @Query("SELECT COUNT(v) FROM Visit v WHERE v.fieldExecutive.id = :fieldExecutiveId " +
            "AND v.visitDate = :date")
    Long countVisitsByFieldExecutiveAndDate(
            @Param("fieldExecutiveId") Long fieldExecutiveId,
            @Param("date") LocalDate date
    );

    @Query("SELECT v FROM Visit v WHERE v.fieldExecutive.id = :fieldExecutiveId " +
            "AND v.visitDate = :date")
    List<Visit> findByFieldExecutiveIdAndVisitDate(
            @Param("fieldExecutiveId") Long fieldExecutiveId,
            @Param("date") LocalDate date
    );

    @Query("""
    SELECT v
    FROM Visit v
    WHERE v.fieldExecutive.id = :feId
      AND v.weekNumber = :weekNumber
      AND v.dayOfWeek = :dayOfWeek
      AND v.scheduledDate BETWEEN :startOfMonth AND :endOfMonth
    ORDER BY v.scheduledDate ASC
""")
    List<Visit> findScheduledVisitsForFieldExecutiveByWeekAndDay(
            @Param("feId") Long fieldExecutiveId,
            @Param("weekNumber") Integer weekNumber,
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("endOfMonth") LocalDateTime endOfMonth
    );


    //-----Portal Lock Methods-----------

    // Field Executive methods
    @Query("""
    SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END
    FROM Visit v
    WHERE v.fieldExecutive = :fe
      AND v.scheduledDate >= :start
      AND v.scheduledDate < :end
      AND v.status IN ('SCHEDULED', 'APPROVED')
""")
    boolean existsByFieldExecutiveAndDate(
            @Param("fe") FieldExecutive fe,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    @Query("""
    SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END
    FROM Visit v
    WHERE v.fieldExecutive = :fe
      AND v.actualVisitTime >= :start
      AND v.actualVisitTime < :end
      AND v.status IN ('COMPLETED', 'MISSED')
""")
    boolean existsCompletedVisitsByFieldExecutiveAndDate(
            @Param("fe") FieldExecutive fe,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    @Query("SELECT CASE WHEN COUNT(mv) > 0 THEN true ELSE false END " +
            "FROM ManagerVisit mv " +
            "WHERE mv.fieldExecutive = :fe " +
            "AND DATE(mv.visitDate) = :date " +
            "AND mv.status = 'COMPLETED'")
    boolean existsManagerVisitForFE(
            @Param("fe") FieldExecutive fe,
            @Param("date") LocalDate date);






}

