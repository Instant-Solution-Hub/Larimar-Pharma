package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.DTOs.MonthlyProductSummaryDto;
import com.instantsolutions.larimarpharma.entity.FEMonthlyProductSales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository

public interface FEMonthlyProductSalesRepository
        extends JpaRepository<FEMonthlyProductSales, Long> {

    List<FEMonthlyProductSales>
    findByFieldExecutiveIdAndYearAndMonth(
            Long feId, Integer year, Integer month
    );

    Optional<FEMonthlyProductSales>
    findByFieldExecutiveIdAndProductIdAndYearAndMonth(
            Long feId, Long productId, Integer year, Integer month
    );

    @Query("""
    SELECT new com.instantsolutions.larimarpharma.DTOs.MonthlyProductSummaryDto(
        p.id,
        p.name,
        p.category,
        p.description,
        p.price,
        p.pts,
        p.ptr,
        COALESCE(SUM(s.sales), 0)
    )
    FROM Product p
    LEFT JOIN FEMonthlyProductSales s
        ON s.product.id = p.id
        AND s.year = :year
        AND s.month = :month
    GROUP BY p.id, p.name, p.category, p.description, p.price, p.pts, p.ptr
""")
    List<MonthlyProductSummaryDto> getMonthlySalesSummary(
            @Param("year") Integer year,
            @Param("month") Integer month
    );

}

