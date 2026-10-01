package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.StockistProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockistProductStockRepository
        extends JpaRepository<StockistProductStock, Long> {

    /*
     * Check whether the FE already has stock for
     * the same stockist + product + month.
     */
    Optional<StockistProductStock>
    findByFieldExecutiveIdAndStockistIdAndProductIdAndStockMonth(
            Long fieldExecutiveId,
            Long stockistId,
            Long productId,
            LocalDate stockMonth
    );

    /*
     * Get all stock records for an FE for a particular month.
     */
    List<StockistProductStock>
    findByFieldExecutiveIdAndStockMonth(
            Long fieldExecutiveId,
            LocalDate stockMonth
    );

    /*
     * Get stock for an FE + particular product + month.
     */
    List<StockistProductStock>
    findByFieldExecutiveIdAndProductIdAndStockMonth(
            Long fieldExecutiveId,
            Long productId,
            LocalDate stockMonth
    );

    /*
     * Get stock for an FE + particular stockist + month.
     */
    List<StockistProductStock>
    findByFieldExecutiveIdAndStockistIdAndStockMonth(
            Long fieldExecutiveId,
            Long stockistId,
            LocalDate stockMonth
    );

    /*
     * Used for update/delete.
     *
     * This prevents FE 5 from modifying a stock record
     * belonging to FE 6.
     */
    Optional<StockistProductStock>
    findByIdAndFieldExecutiveId(
            Long id,
            Long fieldExecutiveId
    );

    @Query("""
        SELECT COALESCE(SUM(s.quantity), 0)
        FROM StockistProductStock s
        WHERE s.fieldExecutive.id = :feId
        AND s.product.id = :productId
        AND s.stockMonth = :month
        """)
    Integer getTotalCurrentMonthStockByFeAndProduct(
            @Param("feId") Long feId,
            @Param("productId") Long productId,
            @Param("month") LocalDate month
    );

    List<StockistProductStock>
    findByFieldExecutiveIdAndStockMonthBetweenOrderByStockMonthAsc(
            Long fieldExecutiveId,
            LocalDate fromMonth,
            LocalDate toMonth
    );


}