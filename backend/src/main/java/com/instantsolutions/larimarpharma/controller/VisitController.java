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

    @PostMapping("/mark-stockist")
    public ResponseEntity<ApiResponseDto<VisitResponseDto>> markStockistVisit(
            @Valid @RequestBody MarkStockistVisitRequestDto request
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                visitService.markStockistVisit(request),
                "Visits Marked successfully"
        ));
    }

    @GetMapping("/planned-doctor-visits")
    public ResponseEntity<List<DoctorVisitSlotDto>> getSlotVisits(
            @RequestParam Long fieldExecutiveId,
            @RequestParam Integer weekNumber,
            @RequestParam Integer dayOfWeek
    ) {
        return ResponseEntity.ok(
                visitService.getSlotVisits(fieldExecutiveId, weekNumber, dayOfWeek)
        );
    }

    @GetMapping("/planned-pharmacy-visits")
    public ResponseEntity<List<PharmacyVisitSlotDto>> getPharmacySlotVisits(
            @RequestParam Long fieldExecutiveId,
            @RequestParam Integer weekNumber,
            @RequestParam Integer dayOfWeek
    ) {
        return ResponseEntity.ok(
                visitService.getPharmacySlotVisits(
                        fieldExecutiveId, weekNumber, dayOfWeek
                )
        );
    }

    @GetMapping("/completed-visits")
    public ResponseEntity<List<CompletedVisitDto>> getCompletedVisits(
            @RequestParam Long fieldExecutiveId
    ) {
        return ResponseEntity.ok(
                visitService.getCompletedVisits(
                        fieldExecutiveId
                )
        );
    }

    /* ===== Scheduled Doctors ===== */
    @GetMapping("/scheduled-doctors")
    public ResponseEntity<List<ScheduledDoctorVisitDto>> getTodayDoctors(
            @RequestParam Long fieldExecutiveId
    ) {
        return ResponseEntity.ok(
                visitService.getTodayScheduledDoctors(fieldExecutiveId)
        );
    }

    /* ===== Scheduled Pharmacies ===== */
    @GetMapping("/scheduled-pharmacies")
    public ResponseEntity<List<ScheduledPharmacyVisitDto>> getTodayPharmacies(
            @RequestParam Long fieldExecutiveId
    ) {
        return ResponseEntity.ok(
                visitService.getTodayScheduledPharmacies(fieldExecutiveId)
        );
    }


    @GetMapping("/today-scheduled")
    public ResponseEntity<List<TodayScheduledVisitDto>> getTodayScheduledVisits(@RequestParam Long fieldExecutiveId) {
        return ResponseEntity.ok(
                visitService.getTodayScheduledVisits(fieldExecutiveId)
        );
    }

    @GetMapping("/get-compliance-record")
    public ResponseEntity<VisitComplianceResponse> getVisitCompliance(
            @RequestParam("fieldExecutiveId") Long fieldExecutiveId,
            @RequestParam(value = "week", defaultValue = "all") String week) {

        VisitComplianceResponse response = visitService.getVisitCompliance(fieldExecutiveId, week);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/monthly-doctor-progress/{feId}")
    public MonthlyDoctorTargetProgressDto getMonthlyDoctorProgress(
            @PathVariable Long feId
    ) {
        return visitService.getMonthlyDoctorTargetProgress(feId);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(
            @PathVariable Long id
    ) {
        visitService.deleteVisit(id);
        return ResponseEntity.ok(
                ApiResponseDto.success(null, "Visit deleted successfully")
        );
    }







}
