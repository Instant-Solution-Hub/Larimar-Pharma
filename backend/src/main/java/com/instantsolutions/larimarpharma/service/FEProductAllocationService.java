package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.FEProductAllocationRequestDto;
import com.instantsolutions.larimarpharma.DTOs.FEProductAllocationResponseDto;
import com.instantsolutions.larimarpharma.DTOs.FEProductStockDto;
import com.instantsolutions.larimarpharma.entity.FEProductAllocation;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.Product;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.FEProductAllocationRepository;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class  FEProductAllocationService {

    private final FEProductAllocationRepository allocationRepository;
    private final ProductService productService;
    private final FieldExecutiveRepository fieldExecutiveRepository;


    @Transactional
    public FEProductStockDto getProductStockForFE(Long feId, Long productId) {

        FEProductAllocation allocation = allocationRepository
                .findByFieldExecutiveIdAndProductId(feId, productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not allocated to this FE")
                );

        return FEProductStockDto.builder()
                .productId(allocation.getProduct().getId())
                .productName(allocation.getProduct().getName())
                .allocatedQuantity(allocation.getAllocatedQuantity())
                .remainingQuantity(allocation.getRemainingQuantity())
                .build();
    }


    public List<FEProductStockDto> getAllAllocatedProducts(Long feId) {

        return allocationRepository.findByFieldExecutiveId(feId)
                .stream()
                .map(a -> FEProductStockDto.builder()
                        .productId(a.getProduct().getId())
                        .productName(a.getProduct().getName())
                        .allocatedQuantity(a.getAllocatedQuantity())
                        .remainingQuantity(a.getRemainingQuantity())
                        .build()
                )
                .toList();
    }


    @Transactional
    public FEProductAllocationResponseDto allocateProductToFE(
            FEProductAllocationRequestDto dto
    ) {
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Allocation quantity must be greater than zero");
        }

        FieldExecutive fe = fieldExecutiveRepository
                .findById(dto.getFeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Field Executive not found with id: " + dto.getFeId()
                        )
                );

        Product product = productService
                .getProductById(dto.getProductId());

               if(product == null) throw new ResourceNotFoundException("Product not found for the given product id");

        FEProductAllocation allocation = allocationRepository
                .findByFieldExecutiveIdAndProductId(
                        dto.getFeId(),
                        dto.getProductId()
                )
                .orElse(null);

        if (allocation == null) {
            // First-time allocation
            allocation = FEProductAllocation.builder()
                    .fieldExecutive(fe)
                    .product(product)
                    .allocatedQuantity(dto.getQuantity())
                    .remainingQuantity(dto.getQuantity())
                    .build();
        } else {
            // Re-allocation
            allocation.setAllocatedQuantity(
                    allocation.getAllocatedQuantity() + dto.getQuantity()
            );
            allocation.setRemainingQuantity(
                    allocation.getRemainingQuantity() + dto.getQuantity()
            );
        }

        FEProductAllocation saved = allocationRepository.save(allocation);

        return FEProductAllocationResponseDto.builder()
                .feId(fe.getId())
                .productId(product.getId())
                .productName(product.getName())
                .allocatedQuantity(saved.getAllocatedQuantity())
                .remainingQuantity(saved.getRemainingQuantity())
                .build();
    }



}