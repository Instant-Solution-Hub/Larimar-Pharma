package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.FEMarketMonthlySales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface FEMarketMonthlySalesRepository
        extends JpaRepository<FEMarketMonthlySales, Long> {

    List<FEMarketMonthlySales> findByFieldExecutiveIdAndYearAndMonth(
            Long fieldExecutiveId,
            int year,
            int month
    );

    Optional<FEMarketMonthlySales> findByFieldExecutiveIdAndMarketAndYearAndMonth(
            Long fieldExecutiveId,
            String market,
            int year,
            int month
    );

    List<FEMarketMonthlySales> findByYearAndMonth(int year, int month);
}

