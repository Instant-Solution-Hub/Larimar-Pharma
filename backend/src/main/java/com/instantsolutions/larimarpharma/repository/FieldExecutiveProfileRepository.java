package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.DTOs.FEMonthlyTargetResponseDto;
import com.instantsolutions.larimarpharma.DTOs.ManagerTargetStatsDto;
import com.instantsolutions.larimarpharma.entity.FieldExecutiveProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FieldExecutiveProfileRepository
        extends JpaRepository<FieldExecutiveProfile, Long> {

    Optional<FieldExecutiveProfile> findByFieldExecutiveId(Long fieldExecutiveId);

    Optional<FieldExecutiveProfile>
    findByFieldExecutiveIdAndMonthAndYear(
            Long fieldExecutiveId,
            Integer month,
            Integer year
    );

    @Query("""
SELECT new com.instantsolutions.larimarpharma.DTOs.FEMonthlyTargetResponseDto(
    fe.id,
    fe.name,
    fe.territory,
    COALESCE(p.primaryTargetSet, 0),
    COALESCE(p.secondaryTargetSet, 0)
)
FROM FieldExecutive fe
LEFT JOIN FieldExecutiveProfile p
    ON p.fieldExecutive = fe
   AND p.month = :month
   AND p.year = :year
WHERE fe.manager.id = :managerId
""")
    List<FEMonthlyTargetResponseDto> findFEMonthlyTargets(
            @Param("managerId") Long managerId,
            @Param("month") Integer month,
            @Param("year") Integer year
    );



    @Query("""
    SELECT new com.instantsolutions.larimarpharma.DTOs.ManagerTargetStatsDto(
        COALESCE(
            SUM(COALESCE(fp.primaryTargetSet, 0d) + COALESCE(fp.secondaryTargetSet, 0d)),
            0d
        ),
        COALESCE(
            SUM(COALESCE(fp.primaryTargetAchieved, 0d) + COALESCE(fp.secondaryTargetAchieved, 0d)),
            0d
        ),
        COUNT(DISTINCT fp.fieldExecutive.id)
    )
    FROM FieldExecutiveProfile fp
    WHERE fp.fieldExecutive.manager.id = :managerId
      AND fp.month = :month
      AND fp.year = :year
""")
    ManagerTargetStatsDto getManagerMonthlyTargets(
            @Param("managerId") Long managerId,
            @Param("month") int month,
            @Param("year") int year
    );



    @Query("""
        SELECT COALESCE(SUM(p.primaryTargetSet), 0)
        FROM FieldExecutiveProfile p
        JOIN p.fieldExecutive fe
        WHERE fe.manager.id = :managerId
          AND fe.territory = :territory
          AND p.month = :month
          AND p.year = :year
    """)
    double sumPrimaryTargetSet(
            @Param("managerId") Long managerId,
            @Param("territory") String territory,
            @Param("month") Integer month,
            @Param("year") Integer year
    );

    @Query("""
        SELECT COALESCE(SUM(p.secondaryTargetSet), 0)
        FROM FieldExecutiveProfile p
        JOIN p.fieldExecutive fe
        WHERE fe.manager.id = :managerId
          AND fe.territory = :territory
          AND p.month = :month
          AND p.year = :year
    """)
    double sumSecondaryTargetSet(
            @Param("managerId") Long managerId,
            @Param("territory") String territory,
            @Param("month") Integer month,
            @Param("year") Integer year
    );

}
