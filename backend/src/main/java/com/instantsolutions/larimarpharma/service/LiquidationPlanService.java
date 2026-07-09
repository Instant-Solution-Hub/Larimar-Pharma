package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.LiquidationPlanRequestDto;
import com.instantsolutions.larimarpharma.DTOs.LiquidationPlanResponseDto;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.exceptions.BadRequestException;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @Autowired
    FEProductAllocationRepository allocationRepository;


    public LiquidationPlanResponseDto create(Long feId, LiquidationPlanRequestDto dto) {

        validateRequest(feId,dto);



//        validateStock(feId, dto.getProductId(), dto.getTargetLiquidation(), null);

        LiquidationPlan plan = LiquidationPlan.builder()
                .fieldExecutive(fieldExecutiveRepository.getReferenceById(feId))
                .product(productRepository.getReferenceById(dto.getProductId()))
                .doctor(doctorRepository.getReferenceById(dto.getDoctorId()))
                .medicalShopName(dto.getMedicalShopName())
                .marketName(dto.getMarketName())
                .targetLiquidation(dto.getTargetLiquidation())
                .deadline(dto.getDeadline())
                .strategy(dto.getStrategy())
                .status(LiquidationPlan.PlanStatus.ACTIVE)
                .availableUnits(allocationRepository.findByFieldExecutiveIdAndProductIdAndMonthAndYear(feId,dto.getProductId(),getCurrentMonth(),getCurrentYear()).get().getAllocatedQuantity())
                .achievedUnits(0)
                .createdAt(LocalDateTime.now())
                .build();

        liquidationPlanRepository.save(plan);
        return mapToResponse(plan);
    }

    private void validateRequest(Long feId, LiquidationPlanRequestDto dto) {

        // 1️⃣ Fetch FE
        FieldExecutive fe = fieldExecutiveRepository.findById(feId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Executive ID"));

        // 2️⃣ Fetch Product
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Product ID"));

        // 3️⃣ Fetch Doctor
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Doctor ID"));

        // 4️⃣ Validate target
        if (dto.getTargetLiquidation() == null || dto.getTargetLiquidation() <= 0) {
            throw new IllegalArgumentException("Target liquidation must be greater than 0");
        }

        // 5️⃣ Validate deadline
        if (dto.getDeadline() == null || dto.getDeadline().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Deadline must be a future date");
        }




        // 7️⃣ Check stock availability
      Optional<FEProductAllocation> allocation = allocationRepository.findByFieldExecutiveIdAndProductIdAndMonthAndYear(feId,dto.getProductId(),getCurrentMonth(),getCurrentYear());
        if(allocation.isEmpty()) throw new IllegalStateException(
                "No stock allocated for Executive: " + feId +
                        ", Required: " + dto.getTargetLiquidation()
        );
        long availableStock = allocation.get().getAllocatedQuantity();

        if (availableStock < dto.getTargetLiquidation()) {
            throw new IllegalStateException(
                    "Insufficient stock. Available: " + availableStock +
                            ", Required: " + dto.getTargetLiquidation()
            );
        }

        LocalDateTime start = YearMonth.now()
                .atDay(1)
                .atStartOfDay();

        LocalDateTime end = YearMonth.now()
                .atEndOfMonth()
                .atTime(23, 59, 59);

        // 8️⃣ Check duplicate active plan
        liquidationPlanRepository
                .findByFieldExecutiveAndProductAndDoctorAndStatusAndCreatedAtBetween(
                        fe, product, doctor, LiquidationPlan.PlanStatus.ACTIVE , start , end
                )
                .ifPresent(p -> {
                    throw new IllegalStateException(
                            "An active liquidation plan already exists for this product and doctor"
                    );
                });


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
        existing.setMarketName(dto.getMarketName());
        existing.setLiquidated1(dto.getLiquidated1());
        existing.setLiquidated2(dto.getLiquidated2());
        existing.setLiquidated3(dto.getLiquidated3());


        return mapToResponse2(existing);
    }

    @Transactional
    public List<LiquidationPlanResponseDto> getCurrentMonthPlansByFE(Long feId) {

        LocalDateTime start = YearMonth.now()
                .atDay(1)
                .atStartOfDay();

        LocalDateTime end = YearMonth.now()
                .atEndOfMonth()
                .atTime(23, 59, 59);

        return liquidationPlanRepository
                .findByFieldExecutiveIdAndCreatedAtBetween(feId, start, end)
                .stream()
                .map(this::mapToResponse2)
                .toList();
    }

    @Transactional
    public LiquidationPlanResponseDto updateApprovalStatus(
            Long planId,
            LiquidationPlan.ApprovalStatus status
    ) {
        LiquidationPlan plan = liquidationPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidation plan not found"));

        plan.setManagerApprovalStatus(status);
        if (status == LiquidationPlan.ApprovalStatus.APPROVED) {
            plan.setAchievedUnits(plan.getTargetLiquidation());
        }

        liquidationPlanRepository.save(plan);

        return mapToResponse(plan);
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

    @Transactional
    public List<LiquidationPlanResponseDto> getCurrentMonthPlans() {

        LocalDateTime start = YearMonth.now()
                .atDay(1)
                .atStartOfDay();

        LocalDateTime end = YearMonth.now()
                .atEndOfMonth()
                .atTime(23, 59, 59);

        return liquidationPlanRepository
                .findByCreatedAtBetween(start, end)
                .stream()
                .map(this::mapToResponse3)
                .toList();
    }


    @Transactional
    public List<LiquidationPlanResponseDto> getPlansBetweenDates(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(LocalTime.MAX);

        return liquidationPlanRepository
                .findByCreatedAtBetween(from, to)
                .stream()
                .map(this::mapToResponse3)
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
        Optional<FEProductAllocation> allocation = allocationRepository.findByFieldExecutiveIdAndProductIdAndMonthAndYear(feId,productId,getCurrentMonth(),getCurrentYear());
        if(allocation.isEmpty()) throw new IllegalStateException(
                "No stock allocated for Executive: " + feId +
                        ", Required: " + newUnits
        );
        long currentStock = allocation.get().getAllocatedQuantity();


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
                .marketName(plan.getMarketName())
                .quantity(plan.getAvailableUnits())
                .managerApprovalStatus(plan.getManagerApprovalStatus())
                .build();
    }

    private LiquidationPlanResponseDto mapToResponse2(LiquidationPlan plan) {
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
                .marketName(plan.getMarketName())
                .quantity(plan.getAvailableUnits())
                .managerApprovalStatus(plan.getManagerApprovalStatus())
                .liquidated1(plan.getLiquidated1())
                .liquidated2(plan.getLiquidated2())
                .liquidated3(plan.getLiquidated3())
                .build();
    }

    private LiquidationPlanResponseDto mapToResponse3(LiquidationPlan plan) {
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
                .marketName(plan.getMarketName())
                .quantity(plan.getAvailableUnits())
                .managerApprovalStatus(plan.getManagerApprovalStatus())
                .liquidated1(plan.getLiquidated1())
                .liquidated2(plan.getLiquidated2())
                .liquidated3(plan.getLiquidated3())
                .employeeId(plan.getFieldExecutive().getId())
                .build();
    }

    private int getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }

    private int getCurrentYear() {
        return LocalDate.now().getYear();
    }
}
