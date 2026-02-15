package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.FEProductAllocationRequestDto;
import com.instantsolutions.larimarpharma.DTOs.FEProductAllocationResponseDto;
import com.instantsolutions.larimarpharma.DTOs.FEProductStockDto;
import com.instantsolutions.larimarpharma.DTOs.UpdateFEProductStockRequestDto;
import com.instantsolutions.larimarpharma.entity.FEProductAllocation;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.LiquidationPlan;
import com.instantsolutions.larimarpharma.entity.Product;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.FEProductAllocationRepository;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.LiquidationPlanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class  FEProductAllocationService {

    private final FEProductAllocationRepository allocationRepository;
    private final ProductService productService;
    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final LiquidationPlanRepository liquidationPlanRepository;


    @Transactional
    public FEProductStockDto getProductStockForFE(Long feId, Long productId) {

        int month = getCurrentMonth();
        int year = getCurrentYear();

        FEProductAllocation allocation = allocationRepository
                .findByFieldExecutiveIdAndProductIdAndMonthAndYear(
                        feId, productId, month, year)
                .orElseThrow(() ->
                        new RuntimeException("Product not allocated to this FE for this month")
                );

        return FEProductStockDto.builder()
                .productId(allocation.getProduct().getId())
                .productName(allocation.getProduct().getName())
                .allocatedQuantity(allocation.getAllocatedQuantity())
                .remainingQuantity(allocation.getRemainingQuantity())
                .build();
    }



    @Transactional
    public List<FEProductStockDto> getAllAllocatedProducts(Long feId) {

        int month = getCurrentMonth();
        int year = getCurrentYear();

        FieldExecutive fe = fieldExecutiveRepository.findById(feId)
                .orElseThrow(() -> new ResourceNotFoundException("FE not found"));

        // 1. Get all products
        List<Product> allProducts = productService.getAllProducts();

        // 2. Get existing allocations for this FE + month + year
        List<FEProductAllocation> existingAllocations =
                allocationRepository.findByFieldExecutiveIdAndMonthAndYear(feId, month, year);

        // Convert to map for quick lookup
        Map<Long, FEProductAllocation> allocationMap =
                existingAllocations.stream()
                        .collect(Collectors.toMap(
                                a -> a.getProduct().getId(),
                                a -> a
                        ));

        List<FEProductAllocation> newAllocations = new ArrayList<>();

        // 3. Ensure allocation exists for every product
        for (Product product : allProducts) {

            if (!allocationMap.containsKey(product.getId())) {

                FEProductAllocation zeroAllocation = FEProductAllocation.builder()
                        .fieldExecutive(fe)
                        .product(product)
                        .month(month)
                        .year(year)
                        .allocatedQuantity(0)
                        .remainingQuantity(0)
                        .build();

                newAllocations.add(zeroAllocation);
                allocationMap.put(product.getId(), zeroAllocation);
            }
        }

        // 4. Persist new zero allocations
        if (!newAllocations.isEmpty()) {
            allocationRepository.saveAll(newAllocations);
        }

        // 5. Return DTO list
        return allocationMap.values().stream()
                .map(a -> FEProductStockDto.builder()
                        .productId(a.getProduct().getId())
                        .productName(a.getProduct().getName())
                        .allocatedQuantity(a.getAllocatedQuantity())
                        .remainingQuantity(a.getRemainingQuantity())
                        .build())
                .toList();
    }



    @Transactional
    public FEProductAllocationResponseDto updateAllocatedProductStock(
            UpdateFEProductStockRequestDto dto
    ) {
        int month = getCurrentMonth();
        int year = getCurrentYear();

        FEProductAllocation allocation = allocationRepository
                .findByFieldExecutiveIdAndProductIdAndMonthAndYear(
                        dto.getFeId(),
                        dto.getProductId(),
                        month,
                        year
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not allocated to this FE")
                );


        int alreadyUsed =
                allocation.getAllocatedQuantity() - allocation.getRemainingQuantity();

        if (dto.getNewAllocatedQuantity() < alreadyUsed) {
            throw new IllegalStateException(
                    "New allocation cannot be less than already utilized quantity: " + alreadyUsed
            );
        }

        allocation.setAllocatedQuantity(dto.getNewAllocatedQuantity());
        allocation.setRemainingQuantity(
                dto.getNewAllocatedQuantity() - alreadyUsed
        );
        List<LiquidationPlan> plans =
                liquidationPlanRepository
                        .findByFieldExecutiveIdAndProductIdAndManagerApprovalStatusIn(
                                dto.getFeId(),
                                dto.getProductId(),
                                List.of(LiquidationPlan.ApprovalStatus.PENDING, LiquidationPlan.ApprovalStatus.APPROVED)
                        );

        for (LiquidationPlan plan : plans) {
            int achievableQty =
                    Math.max(0, allocation.getRemainingQuantity() - plan.getAchievedUnits());

            // Cap target liquidation if stock reduced
            if (plan.getTargetLiquidation() > achievableQty) {
                plan.setTargetLiquidation(achievableQty);
            }
            plan.setAvailableUnits(allocation.getAllocatedQuantity());
        }

        liquidationPlanRepository.saveAll(plans);

        allocationRepository.save(allocation);

        return FEProductAllocationResponseDto.builder()
                .feId(dto.getFeId())
                .productId(dto.getProductId())
                .productName(allocation.getProduct().getName())
                .allocatedQuantity(allocation.getAllocatedQuantity())
                .remainingQuantity(allocation.getRemainingQuantity())
                .build();
    }


    @Transactional
    public FEProductAllocationResponseDto allocateProductToFE(
            FEProductAllocationRequestDto dto
    ) {

        int month = getCurrentMonth();
        int year = getCurrentYear();

        FieldExecutive fe = fieldExecutiveRepository
                .findById(dto.getFeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Field Executive not found")
                );

        Product product = productService.getProductById(dto.getProductId());

        FEProductAllocation allocation = allocationRepository
                .findByFieldExecutiveIdAndProductIdAndMonthAndYear(
                        dto.getFeId(),
                        dto.getProductId(),
                        month,
                        year
                )
                .orElse(null);

        if (allocation == null) {
            allocation = FEProductAllocation.builder()
                    .fieldExecutive(fe)
                    .product(product)
                    .month(month)
                    .year(year)
                    .allocatedQuantity(dto.getQuantity())
                    .remainingQuantity(dto.getQuantity())
                    .build();
        } else {
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


    private int getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }

    private int getCurrentYear() {
        return LocalDate.now().getYear();
    }




}