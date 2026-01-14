package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.LiquidationPlanRequestDto;
import com.instantsolutions.larimarpharma.DTOs.LiquidationPlanResponseDto;
import com.instantsolutions.larimarpharma.service.LiquidationPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
