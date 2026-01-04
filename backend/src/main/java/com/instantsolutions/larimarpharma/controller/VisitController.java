package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.entity.Visit;
import com.instantsolutions.larimarpharma.service.VisitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visit")
public class VisitController {
    @Autowired
    VisitService visitService;

    @GetMapping("/dashboard/{feId}")
    public VisitDashboardResponse getVisitDashboard(@PathVariable Long feId) {
        return visitService.getDashboard(feId);
    }

    @PostMapping("/plan/week-day")
    public ResponseEntity<ApiResponseDto<VisitResponseDto>> planVisitByWeek(
            @Valid @RequestBody VisitPlanByWeekDto request
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                visitService.planVisitByWeek(request),
                "Visits Planned successfully"
        ));
    }

    @PostMapping("/mark")
    public ResponseEntity<ApiResponseDto<VisitResponseDto>> markVisit(
            @Valid @RequestBody MarkVisitRequestDto request
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                visitService.markVisit(request),
                "Visits Marked successfully"
        ));
    }

    @GetMapping("/planned-visits")
    public ResponseEntity<List<DoctorVisitSlotDto>> getSlotVisits(
            @RequestParam Long fieldExecutiveId,
            @RequestParam Integer weekNumber,
            @RequestParam Integer dayOfWeek
    ) {
        return ResponseEntity.ok(
                visitService.getSlotVisits(fieldExecutiveId, weekNumber, dayOfWeek)
        );
    }




}
