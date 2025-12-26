package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.FESalesProgressRequestDto;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.FESalesProgress;
import com.instantsolutions.larimarpharma.entity.Product;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.FESalesProgressRepository;
import com.instantsolutions.larimarpharma.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FESalesProgressService {

    @Autowired
    FESalesProgressRepository repository;
    @Autowired
     ProductRepository productRepository;

    @Autowired
     FieldExecutiveRepository fieldExecutiveRepository;

    /**
     * Create or Update monthly sales progress
     */
    @Transactional
    public FESalesProgress saveOrUpdate(
            Long feId,
            FESalesProgressRequestDto request) {

        FieldExecutive fe = fieldExecutiveRepository.findById(feId)
                .orElseThrow(() -> new ResourceNotFoundException("Field Executive not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Double priceToUse =
                request.getPrice() != null ? request.getPrice() : product.getPrice();

        return repository
                .findByFieldExecutiveIdAndProductIdAndSalesMonth(
                        feId, product.getId(), request.getSalesMonth()
                )
                .map(existing -> {
                    existing.setQuantity(request.getQuantity());
                    existing.setPrice(priceToUse);
                    return existing;
                })
                .orElseGet(() ->
                        repository.save(
                                FESalesProgress.builder()
                                        .fieldExecutive(fe)
                                        .product(product)
                                        .salesMonth(request.getSalesMonth())
                                        .quantity(request.getQuantity())
                                        .price(priceToUse)
                                        .build()
                        )
                );
    }

    /**
     * Get FE's sales progress for a month
     */
    @Transactional(readOnly = true)
    public List<FESalesProgress> getMyMonthlyProgress(
            Long feId,
            YearMonth month) {

        return repository.findAllByFieldExecutiveIdAndSalesMonth(feId, month);
    }
}
