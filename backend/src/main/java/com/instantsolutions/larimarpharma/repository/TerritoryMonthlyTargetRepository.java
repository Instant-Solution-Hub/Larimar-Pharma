package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.TerritoryMonthlyTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TerritoryMonthlyTargetRepository
        extends JpaRepository<TerritoryMonthlyTarget, Long> {

    List<TerritoryMonthlyTarget>
    findByManagerIdAndMonthAndYear(
            Long managerId,
            Integer month,
            Integer year
    );

    Optional<TerritoryMonthlyTarget>
    findByManagerIdAndTerritoryAndMonthAndYear(
            Long managerId,
            String territory,
            Integer month,
            Integer year
    );

    List<TerritoryMonthlyTarget> findByMonthAndYear(int month, int year);

    @Query("""
       SELECT t 
       FROM TerritoryMonthlyTarget t
       JOIN FETCH t.manager
       WHERE t.month = :month
       AND t.year = :year
       """)
    List<TerritoryMonthlyTarget> findAllWithManagerByMonthAndYear(
            @Param("month") int month,
            @Param("year") int year
    );


}
