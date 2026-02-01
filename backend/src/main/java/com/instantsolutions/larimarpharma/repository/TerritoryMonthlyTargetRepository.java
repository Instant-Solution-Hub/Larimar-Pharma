package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.TerritoryMonthlyTarget;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
