package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.StockistProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StockistProductStockRepository
        extends JpaRepository<StockistProductStock, Long> {

    Optional<StockistProductStock> findByStockistIdAndProductId(Long stockistId, Long productId);

    List<StockistProductStock> findAllByStockistId(Long stockistId);

    @Query("""
        SELECT COALESCE(SUM(s.quantity), 0)
        FROM StockistProductStock s
        WHERE s.stockist.fieldExecutive.id = :feId
          AND s.product.id = :productId
    """)
    Integer getTotalStockForFEAndProduct(Long feId, Long productId);

}
