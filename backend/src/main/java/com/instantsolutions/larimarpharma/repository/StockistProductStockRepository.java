package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.StockistProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockistProductStockRepository
        extends JpaRepository<StockistProductStock, Long> {

    Optional<StockistProductStock> findByStockistIdAndProductId(Long stockistId, Long productId);

    List<StockistProductStock> findAllByStockistId(Long stockistId);

    @Query("""
        SELECT COALESCE(SUM(sps.availableQuantity), 0)
        FROM StockistProductStock sps
        JOIN sps.stockist st
        JOIN st.fieldExecutives fe
        WHERE fe.id = :feId
          AND sps.productId = :productId
    """)
    Integer getTotalStockForFEAndProduct(
            @Param("feId") Long feId,
            @Param("productId") Long productId
    );

}
