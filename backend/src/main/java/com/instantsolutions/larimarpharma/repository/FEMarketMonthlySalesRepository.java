package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.DTOs.MarketSalesDetailDto;
import com.instantsolutions.larimarpharma.entity.FEMarketMonthlySales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
    SELECT new com.instantsolutions.larimarpharma.DTOs.MarketSalesDetailDto(
        fe.name,
        ms.market,
        ms.salesAmount
    )
    FROM FEMarketMonthlySales ms
    JOIN ms.fieldExecutive fe
    WHERE ms.year = :year
      AND ms.month = :month
    ORDER BY fe.name ASC, ms.market ASC
""")
    List<MarketSalesDetailDto> findCurrentMonthMarketSalesDetail(
            @Param("year") int year,
            @Param("month") int month
    );

    Optional<FEMarketMonthlySales> findByFieldExecutiveIdAndMarketAndYearAndMonth(
            Long fieldExecutiveId,
            String market,
            int year,
            int month
    );

    List<FEMarketMonthlySales> findByYearAndMonth(int year, int month);
}

