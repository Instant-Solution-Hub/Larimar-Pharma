package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.StockUpdateRequestDto;
import com.instantsolutions.larimarpharma.DTOs.StockistProductStockRequestDto;
import com.instantsolutions.larimarpharma.DTOs.StockistProductStockResponseDto;
import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.entity.Product;
import com.instantsolutions.larimarpharma.entity.Stockist;
import com.instantsolutions.larimarpharma.entity.StockistProductStock;
import com.instantsolutions.larimarpharma.repository.*;
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
    ManagerRepository managerRepo;

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

    @Transactional
    public StockistProductStockResponseDto addStock(
            StockistProductStockRequestDto dto
    ) {
        Stockist stockist = validateManagerStockist(
                dto.getManagerId(), dto.getStockistId()
        );

        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        stockRepo.findByStockistIdAndProductId(
                dto.getStockistId(), dto.getProductId()
        ).ifPresent(s -> {
            throw new RuntimeException("Stock already exists");
        });

        StockistProductStock stock = StockistProductStock.builder()
                .stockist(stockist)
                .product(product)
                .availableQuantity(dto.getQuantity())
                .build();

        return mapToDto(stockRepo.save(stock));
    }

    @Transactional
    public StockistProductStockResponseDto updateStock(
            StockistProductStockRequestDto dto
    ) {
        validateManagerStockist(dto.getManagerId(), dto.getStockistId());

        StockistProductStock stock = stockRepo
                .findByStockistIdAndProductId(
                        dto.getStockistId(), dto.getProductId()
                )
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        stock.setAvailableQuantity(dto.getQuantity());

        return mapToDto(stockRepo.save(stock));
    }

    @Transactional
    public void deleteStock(Long managerId ,Long stockistId, Long productId) {
        validateManagerStockist(managerId, stockistId);
        StockistProductStock stock = stockRepo
                .findByStockistIdAndProductId(stockistId, productId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        stockRepo.delete(stock);
    }


    @Transactional
    private Stockist validateManagerStockist(
            Long managerId,
            Long stockistId
    ) {
        Manager manager = managerRepo.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        Stockist stockist = stockistRepo.findById(stockistId)
                .orElseThrow(() -> new RuntimeException("Stockist not found"));

        if (!stockist.getManagers().contains(manager)) {
            throw new RuntimeException("Stockist not assigned to this manager");
        }

        return stockist;
    }

    @Transactional
    public List<StockistProductStockResponseDto> getAllStocksUnderManager(
            Long managerId
    ) {
        // Validate manager exists (optional but recommended)
        managerRepo.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        return stockRepo.findAllStocksByManagerId(managerId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }


    private StockistProductStockResponseDto mapToDto(
            StockistProductStock stock
    ) {
        return StockistProductStockResponseDto.builder()
                .id(stock.getId())
                .stockistId(stock.getStockist().getId())
                .stockistName(stock.getStockist().getName())
                .productId(stock.getProduct().getId())
                .productName(stock.getProduct().getName())
                .availableQuantity(stock.getAvailableQuantity())
                .updatedAt(stock.getUpdatedAt())
                .marketName(stock.getStockist().getLocation())
                .build();
    }


}

