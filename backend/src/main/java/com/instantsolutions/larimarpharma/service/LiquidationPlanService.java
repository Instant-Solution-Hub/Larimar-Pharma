package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.LiquidationPlanRequestDto;
import com.instantsolutions.larimarpharma.DTOs.LiquidationPlanResponseDto;
import com.instantsolutions.larimarpharma.entity.LiquidationPlan;
import com.instantsolutions.larimarpharma.exceptions.BadRequestException;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LiquidationPlanService {

   @Autowired
   LiquidationPlanRepository liquidationPlanRepository;
   @Autowired
   StockistProductStockRepository stockRepository;
    @Autowired
    ProductRepository productRepository;

    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    FieldExecutiveRepository fieldExecutiveRepository;


    public LiquidationPlanResponseDto create(Long feId, LiquidationPlanRequestDto dto) {

        validateStock(feId, dto.getProductId(), dto.getTargetLiquidation(), null);

        LiquidationPlan plan = LiquidationPlan.builder()
                .fieldExecutive(fieldExecutiveRepository.getReferenceById(feId))
                .product(productRepository.getReferenceById(dto.getProductId()))
                .doctor(doctorRepository.getReferenceById(dto.getDoctorId()))
                .medicalShopName(dto.getMedicalShopName())
                .targetLiquidation(dto.getTargetLiquidation())
                .deadline(dto.getDeadline())
                .strategy(dto.getStrategy())
                .build();

        liquidationPlanRepository.save(plan);
        return mapToResponse(plan);
    }


    public LiquidationPlanResponseDto update(
            Long planId,
            Long feId,
            LiquidationPlanRequestDto dto
    ) {

        LiquidationPlan existing = liquidationPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidation plan not found"));

        if (!existing.getFieldExecutive().getId().equals(feId)) {
            throw new BadRequestException("Unauthorized update");
        }

        validateStock(
                feId,
                existing.getProduct().getId(),
                dto.getTargetLiquidation(),
                existing.getTargetLiquidation()
        );

        existing.setTargetLiquidation(dto.getTargetLiquidation());
        existing.setDeadline(dto.getDeadline());
        existing.setStrategy(dto.getStrategy());
        existing.setMedicalShopName(dto.getMedicalShopName());

        return mapToResponse(existing);
    }


    @Transactional
    public List<LiquidationPlanResponseDto> getByFeAndProductCurrentMonth(
            Long feId,
            Long productId
    ) {

        LocalDateTime start = YearMonth.now().atDay(1).atStartOfDay();
        LocalDateTime end = YearMonth.now().atEndOfMonth().atTime(23, 59, 59);

        return liquidationPlanRepository
                .findByFieldExecutiveIdAndProductIdAndCreatedAtBetween(
                        feId, productId, start, end
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /* ===================== VALIDATION ===================== */

    private void validateStock(
            Long feId,
            Long productId,
            Integer newUnits,
            Integer oldUnits
    ) {

        LocalDateTime start = YearMonth.now().atDay(1).atStartOfDay();
        LocalDateTime end = YearMonth.now().atEndOfMonth().atTime(23, 59, 59);

        int currentStock =
                stockRepository.getTotalStockForFEAndProduct(feId, productId);

        int usedUnits =
                liquidationPlanRepository.getUsedUnitsForMonth(
                        feId, productId, start, end
                );

        if (oldUnits != null) {
            usedUnits -= oldUnits;
        }

        int finalUnits = usedUnits + newUnits;

        if (finalUnits < 0) {
            throw new BadRequestException("Liquidation units cannot be negative");
        }

        if (finalUnits > currentStock) {
            throw new BadRequestException(
                    "Cannot liquidate more than available stock. Available: "
                            + currentStock + ", Requested: " + finalUnits
            );
        }
    }

    private LiquidationPlanResponseDto mapToResponse(LiquidationPlan plan) {
        return LiquidationPlanResponseDto.builder()
                .id(plan.getId())
                .productId(plan.getProduct().getId())
                .productName(plan.getProduct().getName())
                .doctorId(plan.getDoctor().getId())
                .doctorName(plan.getDoctor().getName())
                .targetLiquidation(plan.getTargetLiquidation())
                .achievedUnits(plan.getAchievedUnits())
                .medicalShopName(plan.getMedicalShopName())
                .deadline(plan.getDeadline())
                .strategy(plan.getStrategy())
                .createdAt(plan.getCreatedAt())
                .build();
    }
}
