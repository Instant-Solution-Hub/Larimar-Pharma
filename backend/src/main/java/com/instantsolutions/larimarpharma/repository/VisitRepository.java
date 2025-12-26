package com.instantsolutions.larimarpharma.repository;

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
            COUNT(CASE WHEN v.visitType = 'DOCTOR' THEN 1 END),
            COUNT(CASE WHEN v.visitType = 'PHARMACIST' THEN 1 END),
            COUNT(CASE WHEN v.visitType = 'STOCKIST' THEN 1 END)
        FROM Visit v
        WHERE v.fieldExecutive.id = :feId
          AND v.status = com.instantsolutions.larimarpharma.entity.Visit.VisitStatus.COMPLETED
          AND v.actualDate BETWEEN :startDate AND :endDate
    """)
    Object[] getCompletedVisitCountsForMonth(
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
}

