package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.entity.Visit;
import com.instantsolutions.larimarpharma.service.VisitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @PostMapping("/plan/week-day/current-month")
    public ResponseEntity<ApiResponseDto<VisitResponseDto>> planVisitByWeekForCurrentMonth(
            @Valid @RequestBody VisitPlanByWeekDto request
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                visitService.planVisitByWeekForCurrentMonth(request),
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

    @PostMapping("/change-status/{visitId}/{status}")
    public ResponseEntity<ApiResponseDto<VisitResponseDto>> changeVisitStatus(
            @PathVariable Long visitId,@PathVariable String status
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                visitService.changeStatus(visitId, status),
                "Visits Marked successfully"
        ));
    }

    @PostMapping("/re-mark")
    public ResponseEntity<VisitResponseDto> reMarkVisit(
            @RequestBody @Valid MarkVisitRequestDto dto
    ) {
        return ResponseEntity.ok(visitService.reMarkVisit(dto));
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

    @GetMapping("/current-planned-doctor-visits")
    public ResponseEntity<List<DoctorVisitSlotDto>> getCurrentMonthSlotVisits(
            @RequestParam Long fieldExecutiveId,
            @RequestParam Integer weekNumber,
            @RequestParam Integer dayOfWeek
    ) {
        return ResponseEntity.ok(
                visitService.getCurrentMonthSlotVisits(fieldExecutiveId, weekNumber, dayOfWeek)
        );
    }

    @GetMapping("/current-planned-pharmacy-visits")
    public ResponseEntity<List<PharmacyVisitSlotDto>> getCurrentMonthPharmacySlotVisits(
            @RequestParam Long fieldExecutiveId,
            @RequestParam Integer weekNumber,
            @RequestParam Integer dayOfWeek
    ) {
        return ResponseEntity.ok(
                visitService.getCurrentMonthPharmacySlotVisits(
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

    @GetMapping("/missed-visits")
    public ResponseEntity<List<CompletedVisitDto>> getMissedVisits(
            @RequestParam Long fieldExecutiveId
    ) {
        return ResponseEntity.ok(
                visitService.getMissedVisits(
                        fieldExecutiveId
                )
        );
    }

    @GetMapping("/get-all-missed-visits")
    public ResponseEntity<List<CompletedVisitDto>> getAllMissedVisits() {
        return ResponseEntity.ok(
                visitService.getAllMissedVisits()
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
                visitService.getTodaysVisits(fieldExecutiveId)
        );
    }

    @GetMapping("/today-scheduled-and-missed")
    public ResponseEntity<List<TodayScheduledVisitDto>> getTodayScheduledAndMissedVisits(@RequestParam Long fieldExecutiveId) {
        return ResponseEntity.ok(
                visitService.getTodaysAndMissedVisits(fieldExecutiveId)
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

    @GetMapping("/manager/{managerId}/scheduled-visits")
    public ResponseEntity<List<TodayScheduledVisitDto>> getScheduledVisitsForManager(
            @PathVariable Long managerId,
            @RequestParam Integer weekNumber,
            @RequestParam Integer dayOfWeek
    ) {
        return ResponseEntity.ok(
                visitService.getScheduledVisitsForManager(
                        managerId,
                        weekNumber,
                        dayOfWeek
                )
        );
    }

    @PostMapping("/get-all-visits-by-week-day")
    public ResponseEntity<ApiResponseDto<List<TodayScheduledVisitDto>>> fetchVisitsByWeekAndDay(
            @RequestParam Long fieldExecutiveId,
            @RequestParam Integer weekNumber,
            @RequestParam Integer dayOfWeek
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                visitService.getVisitsForWeekAndDay(fieldExecutiveId, weekNumber, dayOfWeek),
                "Visits fetched successfully"
        ));
    }

    @GetMapping("/doctor-track")
    public ResponseEntity<DoctorVisitProgressDto> getDoctorVisitTrack(
            @RequestParam("fieldExecutiveId") Long fieldExecutiveId,
            @RequestParam("doctorId") Long doctorId) {

        DoctorVisitProgressDto response = visitService.getDoctorVisitTracking(fieldExecutiveId, doctorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctor-completed-track")
    public ResponseEntity<DoctorVisitProgressDto> getDoctorCompletedVisitTrack(
            @RequestParam("fieldExecutiveId") Long fieldExecutiveId,
            @RequestParam("doctorId") Long doctorId) {

        DoctorVisitProgressDto response = visitService.getDoctorVisitCompletionTracking(fieldExecutiveId, doctorId);
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/visit-report")
//    public ResponseEntity<VisitSummaryResponseDto> getVisitSummary(
//            @RequestParam Long fieldExecutiveId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
//    ) {
//        return ResponseEntity.ok(
//                visitService.getVisitSummary(fieldExecutiveId, from, to)
//        );
//    }

    @GetMapping("/visit-report")
    public ResponseEntity<VisitReportDto> getVisitReport(
            @RequestParam Long fieldExecutiveId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam String status,
            @RequestParam String category
    ) {
        return ResponseEntity.ok(
                visitService.getVisitReport(fieldExecutiveId, from, to, status, category)
        );
    }


    // Admin endpoints

    @PostMapping("/admin/mark-visit-as-completed/{visitId}")
    public ResponseEntity<ApiResponseDto<CompletedVisitDto>> markVisitAsCompleted(@PathVariable Long visitId) {
        CompletedVisitDto  updatedVisit = visitService.markVisitAsCompleted(visitId);
        return ResponseEntity.ok(ApiResponseDto.success(updatedVisit, "Visit Marked as Completed"));
    }








}
