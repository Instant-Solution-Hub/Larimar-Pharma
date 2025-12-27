package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.StockistProductStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockistProductStockRepository
        extends JpaRepository<StockistProductStock, Long> {

    Optional<StockistProductStock> findByStockistIdAndProductId(Long stockistId, Long productId);

    List<StockistProductStock> findAllByStockistId(Long stockistId);

}
