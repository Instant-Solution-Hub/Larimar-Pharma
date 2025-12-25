package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.entity.MonthlyTargetPlan;
import com.instantsolutions.larimarpharma.service.MonthlyTargetPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fe/targets")
@RequiredArgsConstructor
public class FETargetController {

    private final MonthlyTargetPlanService targetPlanService;

    @PostMapping
    public ResponseEntity<MonthlyTargetPlan> createTargetPlan(
            @RequestParam Long feId,
            @RequestParam Integer month,
            @RequestParam Integer year,
            @RequestParam Double primaryTarget,
            @RequestParam Double secondaryTarget
    ) {
        return ResponseEntity.ok(
                targetPlanService.createTargetPlan(
                        feId, month, year, primaryTarget, secondaryTarget
                )
        );
    }
}
