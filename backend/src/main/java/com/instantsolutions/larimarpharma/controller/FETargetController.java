package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.MonthlyTargetPlanRequestDto;
import com.instantsolutions.larimarpharma.entity.MonthlyTargetPlan;
import com.instantsolutions.larimarpharma.service.MonthlyTargetPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fe/targets")
@RequiredArgsConstructor
public class FETargetController {

    private final MonthlyTargetPlanService targetPlanService;

    @PostMapping
    public ResponseEntity<MonthlyTargetPlan> create(
            @Valid @RequestBody MonthlyTargetPlanRequestDto dto
    ) {
        return ResponseEntity.ok(targetPlanService.createTargetPlan(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MonthlyTargetPlan> update(
            @PathVariable Long id,
            @Valid @RequestBody MonthlyTargetPlanRequestDto dto
    ) {
        return ResponseEntity.ok(targetPlanService.update(id, dto));
    }

    @GetMapping("/fe/{feId}")
    public ResponseEntity<List<MonthlyTargetPlan>> getByFe(
            @PathVariable Long feId
    ) {
        return ResponseEntity.ok(
                targetPlanService.getByFieldExecutive(feId)
        );
    }

    @GetMapping
    public ResponseEntity<MonthlyTargetPlan> getForMonth(
            @RequestParam Long feId,
            @RequestParam Integer month,
            @RequestParam Integer year) {

        return ResponseEntity.ok(
                targetPlanService.getForMonth(feId, month, year)
        );
    }
}
