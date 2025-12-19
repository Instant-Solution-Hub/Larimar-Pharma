package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.Visit;
import com.instantsolutions.larimarpharma.entity.Visit.VisitStatus;
import com.instantsolutions.larimarpharma.entity.Visit.VisitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    long countByFieldExecutiveIdAndVisitTypeAndScheduledDateBetween(
            Long feId,
            VisitType visitType,
            LocalDateTime start,
            LocalDateTime end
    );

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
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    List<Visit> findByVisitTypeAndStatusAndScheduledDateBefore(
            VisitType visitType,
            VisitStatus status,
            LocalDateTime now
    );
}

