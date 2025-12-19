package com.instantsolutions.larimarpharma.repository;



import com.instantsolutions.larimarpharma.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface VisitRepository extends JpaRepository<Visit, Long> {

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

    // Total doctor visits (including scheduled + completed)
    long countByFieldExecutiveIdAndVisitTypeAndScheduledDateBetween(
            Long feId,
            Visit.VisitType visitType,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
