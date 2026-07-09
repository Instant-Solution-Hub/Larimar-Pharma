package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.LiquidationPlanRequestDto;
import com.instantsolutions.larimarpharma.DTOs.LiquidationPlanResponseDto;
import com.instantsolutions.larimarpharma.DTOs.UpdateLiquidationApprovalDto;
import com.instantsolutions.larimarpharma.service.LiquidationPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/api/fe/liquidation-plans")
@RequiredArgsConstructor
public class LiquidationPlanController {

    private final LiquidationPlanService liquidationPlanService;

    @PostMapping
    public ResponseEntity<LiquidationPlanResponseDto> create(
            @RequestParam Long feId,
            @RequestBody @Valid LiquidationPlanRequestDto dto
    ) {
        return ResponseEntity.ok(
                liquidationPlanService.create(feId, dto)
        );
    }

    @PutMapping("/{planId}")
    public ResponseEntity<LiquidationPlanResponseDto> update(
            @PathVariable Long planId,
            @RequestParam Long feId,
            @RequestBody @Valid LiquidationPlanRequestDto dto
    ) {
        return ResponseEntity.ok(
                liquidationPlanService.update(planId, feId, dto)
        );
    }

    @GetMapping("/current-month")
    public ResponseEntity<ApiResponseDto<List<LiquidationPlanResponseDto>>> getCurrentMonthPlans() {

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        liquidationPlanService.getCurrentMonthPlans(),
                        "Current month liquidation plans fetched successfully"
                )
        );
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponseDto<List<LiquidationPlanResponseDto>>> getPlansBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        liquidationPlanService.getPlansBetweenDates(fromDate, toDate),
                        "Liquidation plans fetched successfully"
                )
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<LiquidationPlanResponseDto>> getByProduct(
            @RequestParam Long feId,
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                liquidationPlanService.getByFeAndProductCurrentMonth(feId, productId)
        );
    }

    @GetMapping
    public ResponseEntity<List<LiquidationPlanResponseDto>> getCurrentMonthPlans(
            @RequestParam Long feId
    ) {
        return ResponseEntity.ok(
                liquidationPlanService.getCurrentMonthPlansByFE(feId)
        );
    }

    @PutMapping("/{planId}/approval")
    public ResponseEntity<LiquidationPlanResponseDto> updateApprovalStatus(
            @PathVariable Long planId,
            @RequestBody UpdateLiquidationApprovalDto dto
    ) {
        return ResponseEntity.ok(
                liquidationPlanService.updateApprovalStatus(planId, dto.getStatus())
        );
    }

}
