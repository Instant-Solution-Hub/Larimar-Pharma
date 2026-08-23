package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.service.ManagerVisitService;
import com.instantsolutions.larimarpharma.service.ZsmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/zsm-visits")
@RequiredArgsConstructor
public class ZsmVisitController {
    private final ZsmService zsmVisitService;

    @PostMapping("/assign")
    public ResponseEntity<ApiResponseDto<String>> assignZsm(
            @RequestBody @Valid AssignZsmVisitRequest request
    ) {
        zsmVisitService.assignZsmToVisit(request);
        return ResponseEntity.ok(ApiResponseDto.success(null,"Visits assigned successfully"));
    }

    @PostMapping("/unassign")
    public ResponseEntity<ApiResponseDto<String>> unAssignZsm(
            @RequestBody @Valid AssignZsmVisitRequest request
    ) {
        zsmVisitService.unassignFieldExecutiveVisits(request.getFieldExecutiveId(),
                request.getWeekNumber(),
                request.getDayOfWeek());
        return ResponseEntity.ok(ApiResponseDto.success(null,"Visits un-assigned successfully"));
    }

    @PostMapping("/change-status/{visitId}/{status}")
    public ResponseEntity<ApiResponseDto<ZsmVisitDto>> changeStatus(
            @PathVariable Long visitId, @PathVariable String status
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                zsmVisitService.changeStatus(visitId, status),
                "Visits Marked successfully"
        ));
    }

    @PostMapping("/mark")
    public ResponseEntity<ApiResponseDto<ZsmVisitDto>> markVisit(
            @Valid @RequestBody MarkVisitRequestDto request
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                zsmVisitService.markVisit(request),
                "Visits Marked successfully"
        ));
    }

    @PostMapping("/re-mark")
    public ResponseEntity<ZsmVisitDto> reMarkVisit(
            @RequestBody @Valid MarkVisitRequestDto dto
    ) {
        return ResponseEntity.ok(zsmVisitService.reMarkVisit(dto));
    }

    @GetMapping("/today-scheduled")
    public ResponseEntity<List<TodayScheduledVisitDto>> getTodayScheduledVisits(@RequestParam Long zsmId) {
        return ResponseEntity.ok(
                zsmVisitService.getTodaysVisits(zsmId)
        );
    }

    @GetMapping("/today-scheduled-and-missed")
    public ResponseEntity<List<TodayScheduledVisitDto>> getTodayScheduledAndMissedVisits(@RequestParam Long zsmId) {
        return ResponseEntity.ok(
                zsmVisitService.getTodaysAndMissedVisits(zsmId)
        );
    }

    @GetMapping("/today-scheduled-only")
    public ResponseEntity<List<TodayScheduledVisitDto>> getTodayScheduledVisitsOnly(@RequestParam Long zsmId) {
        return ResponseEntity.ok(
                zsmVisitService.getTodaysVisitsScheduledOnly(zsmId)
        );
    }

    @GetMapping("/completed-visits")
    public ResponseEntity<List<CompletedVisitDto>> getCompletedVisits(
            @RequestParam Long zsmId
    ) {
        return ResponseEntity.ok(
                zsmVisitService.getCompletedVisits(
                        zsmId
                )
        );
    }

    @GetMapping("/missed-visits")
    public ResponseEntity<List<CompletedVisitDto>> getMissedVisits(
            @RequestParam Long zsmId
    ) {
        return ResponseEntity.ok(
                zsmVisitService.getMissedVisits(
                        zsmId
                )
        );
    }

    @GetMapping("/get-all-missed-visits")
    public ResponseEntity<List<CompletedVisitDto>> getAllMissedVisits() {
        return ResponseEntity.ok(
                zsmVisitService.getAllMissedVisits()
        );
    }

    @GetMapping("/get-manager-compliance-record")
    public ResponseEntity<VisitComplianceResponse> getZsmVisitCompliance(
            @RequestParam("zsmId") Long zsmId,
            @RequestParam(value = "week", defaultValue = "all") String week) {

        VisitComplianceResponse response =
                zsmVisitService.getZsmVisitCompliance(zsmId, week);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/get-all-visits-by-week-day")
    public ResponseEntity<ApiResponseDto<List<CompletedVisitDto>>> fetchVisitsByWeekAndDay(
            @RequestParam Long zsmId,
            @RequestParam Integer weekNumber,
            @RequestParam Integer dayOfWeek
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                zsmVisitService.getVisitsForZsmWeekAndDay(zsmId, weekNumber, dayOfWeek),
                "Visits fetched successfully"
        ));
    }

    @PostMapping("/get-current-month-visits")
    public ResponseEntity<ApiResponseDto<List<CompletedVisitDto>>> getCurrentMonthVisitsForManager(
            @RequestParam Long zsmId,
            @RequestParam Integer weekNumber,
            @RequestParam Integer dayOfWeek
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                zsmVisitService.getCurrentMonthVisitsForZsm(zsmId, weekNumber, dayOfWeek),
                "Visits fetched successfully"
        ));
    }

    @PostMapping("/admin/mark-visit-as-completed/{visitId}")
    public ResponseEntity<ApiResponseDto<CompletedVisitDto>> markVisitAsCompleted(@PathVariable Long visitId) {
        CompletedVisitDto  updatedVisit = zsmVisitService.markZsmVisitAsCompleted(visitId);
        return ResponseEntity.ok(ApiResponseDto.success(updatedVisit, "Visit Marked as Completed"));
    }

    @GetMapping("/reports")
    public ResponseEntity<VisitReportDto> getZsmVisitSummary(
            @RequestParam Long zsmId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam String status,
            @RequestParam String category,
            @RequestParam String docType
    ) {
        return ResponseEntity.ok(
                zsmVisitService.getZsmVisitReport(zsmId, from, to, status, category,docType)
        );
    }


}
