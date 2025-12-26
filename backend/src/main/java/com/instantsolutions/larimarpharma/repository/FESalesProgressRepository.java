package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.FESalesProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface FESalesProgressRepository
        extends JpaRepository<FESalesProgress, Long> {

    Optional<FESalesProgress> findByFieldExecutiveIdAndProductIdAndSalesMonth(
            Long feId,
            Long productId,
            YearMonth salesMonth
    );

    List<FESalesProgress> findAllByFieldExecutiveIdAndSalesMonth(
            Long feId,
            YearMonth salesMonth
    );
}
