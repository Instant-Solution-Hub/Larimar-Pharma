package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.DTOs.MonthlyStockistSalesSummaryDto;
import com.instantsolutions.larimarpharma.entity.FEMonthlyStockistSales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FEMonthlyStockistSalesRepository
        extends JpaRepository<FEMonthlyStockistSales, Long> {

    List<FEMonthlyStockistSales> findByFieldExecutiveIdAndYearAndMonth(
            Long feId,
            Integer year,
            Integer month
    );

    Optional<FEMonthlyStockistSales>
    findByFieldExecutiveIdAndStockistIdAndYearAndMonth(
            Long feId,
            Long stockistId,
            Integer year,
            Integer month
    );

    @Query("""
       SELECT new com.instantsolutions.larimarpharma.DTOs.MonthlyStockistSalesSummaryDto(
            s.fieldExecutive.name,
            s.fieldExecutive.region,
            s.stockist.name,
            s.price
       )
       FROM FEMonthlyStockistSales s
       WHERE s.year = :year
         AND s.month = :month
         AND s.price IS NOT NULL
         AND s.price <> 0
       ORDER BY s.fieldExecutive.name ASC
       """)
    List<MonthlyStockistSalesSummaryDto> findAllSummaryByMonthAndYear(
            @Param("year") Integer year,
            @Param("month") Integer month
    );
}
