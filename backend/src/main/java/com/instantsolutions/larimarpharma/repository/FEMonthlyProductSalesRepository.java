package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.FEMonthlyProductSales;
import org.springframework.data.jpa.repository.JpaRepository;
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
}

