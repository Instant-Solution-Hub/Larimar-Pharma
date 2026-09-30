package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.StockistProductStock;
import org.springframework.data.jpa.repository.JpaRepository;

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
}