package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.StockUpdateRequestDto;
import com.instantsolutions.larimarpharma.entity.Product;
import com.instantsolutions.larimarpharma.entity.Stockist;
import com.instantsolutions.larimarpharma.entity.StockistProductStock;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.ProductRepository;
import com.instantsolutions.larimarpharma.repository.StockistProductStockRepository;
import com.instantsolutions.larimarpharma.repository.StockistRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockistProductStockService {

    @Autowired
    StockistProductStockRepository stockRepo;
    @Autowired
    StockistRepository stockistRepo;
    @Autowired
    ProductRepository productRepo;
    @Autowired
    FieldExecutiveRepository feRepo;

    @Transactional
    public StockistProductStock addOrUpdateStock(
            Long feId,
            StockUpdateRequestDto request) {

        Stockist stockist = stockistRepo.findById(request.getStockistId())
                .orElseThrow(() -> new EntityNotFoundException("Stockist not found"));

        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));


        StockistProductStock stock = stockRepo
                .findByStockistIdAndProductId(stockist.getId(), product.getId())
                .orElse(
                        StockistProductStock.builder()
                                .stockist(stockist)
                                .productId(request.getProductId())
                                .productName(product.getName())
                                .build()
                );

        stock.setAvailableQuantity(request.getAvailableQuantity());

        return stockRepo.save(stock);
    }

    @Transactional
    public List<StockistProductStock> getStockByStockist(Long stockistId) {
        return stockRepo.findAllByStockistId(stockistId);
    }
}

