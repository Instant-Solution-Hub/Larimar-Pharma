package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.Stockist;
import com.instantsolutions.larimarpharma.entity.StockistProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StockistProductStockRepository
        extends JpaRepository<StockistProductStock, Long> {

    Optional<StockistProductStock> findByStockistIdAndProductId(Long stockistId, Long productId);

    List<StockistProductStock> findAllByStockistId(Long stockistId);
    @Query("""
        SELECT s
        FROM StockistProductStock s
        JOIN FETCH s.product p
        JOIN FETCH s.stockist st
        JOIN st.managers m
        WHERE m.id = :managerId
    """)
    List<StockistProductStock> findAllStocksByManagerId(
            @Param("managerId") Long managerId
    );


}
