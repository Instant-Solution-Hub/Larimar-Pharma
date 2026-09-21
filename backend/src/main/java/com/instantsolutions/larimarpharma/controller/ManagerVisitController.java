package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.service.ManagerService;
import com.instantsolutions.larimarpharma.service.ManagerVisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/manager-visits")
@RequiredArgsConstructor
public class ManagerVisitController {

    private final ManagerVisitService managerVisitService;
    private final ManagerService managerService;

    @PostMapping("/assign")
    public ResponseEntity<ApiResponseDto<String>> assignManager(
            @RequestBody @Valid AssignManagerVisitRequest request
    ) {
        managerVisitService.assignManagerToVisit(request);
        return ResponseEntity.ok(ApiResponseDto.success(null,"Visits assigned successfully"));
    }

    @PostMapping("/unassign")
    public ResponseEntity<ApiResponseDto<String>> unAssignManager(
            @RequestBody @Valid AssignManagerVisitRequest request
    ) {
        managerVisitService.unassignFieldExecutiveVisits(request.getFieldExecutiveId(),
                request.getWeekNumber(),
                request.getDayOfWeek());
        return ResponseEntity.ok(ApiResponseDto.success(null,"Visits un-assigned successfully"));
    }

    @PostMapping("/change-status")
    public ResponseEntity<ApiResponseDto<ManagerVisitDto>> changeStatus(
            @PathVariable Long visitId,@PathVariable String status
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                managerVisitService.changeStatus(visitId, status),
                "Visits Marked successfully"
        ));
    }


    @PostMapping("/mark")
    public ResponseEntity<ApiResponseDto<ManagerVisitDto>> markVisit(
            @Valid @RequestBody MarkVisitRequestDto request
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                managerVisitService.markVisit(request),
                "Visits Marked successfully"
        ));
    }

    @PostMapping("/re-mark")
    public ResponseEntity<ManagerVisitDto> reMarkVisit(
            @RequestBody @Valid MarkVisitRequestDto dto
    ) {
        return ResponseEntity.ok(managerVisitService.reMarkVisit(dto));
    }

    @GetMapping("/today-scheduled")
    public ResponseEntity<List<TodayScheduledVisitDto>> getTodayScheduledVisits(@RequestParam Long managerId) {
        return ResponseEntity.ok(
                managerVisitService.getTodaysVisits(managerId)
        );
    }

    @GetMapping("/today-scheduled-and-missed")
    public ResponseEntity<List<TodayScheduledVisitDto>> getTodayScheduledAndMissedVisits(@RequestParam Long managerId) {
        return ResponseEntity.ok(
                managerVisitService.getTodaysAndMissedVisits(managerId)
        );
    }

    @GetMapping("/today-scheduled-only")
    public ResponseEntity<List<TodayScheduledVisitDto>> getTodayScheduledVisitsOnly(@RequestParam Long managerId) {
        return ResponseEntity.ok(
                managerVisitService.getTodaysVisitsScheduledOnly(managerId)
        );
    }

    @GetMapping("/completed-visits")
    public ResponseEntity<List<CompletedVisitDto>> getCompletedVisits(
            @RequestParam Long managerId
    ) {
        return ResponseEntity.ok(
                managerVisitService.getCompletedVisits(
                        managerId
                )
        );
    }

    @GetMapping("/missed-visits")
    public ResponseEntity<List<CompletedVisitDto>> getMissedVisits(
            @RequestParam Long managerId
    ) {
        return ResponseEntity.ok(
                managerVisitService.getMissedVisits(
                        managerId
                )
        );
    }

    @GetMapping("/get-all-missed-visits")
    public ResponseEntity<List<CompletedVisitDto>> getAllMissedVisits() {
        return ResponseEntity.ok(
                managerVisitService.getAllMissedVisits()
        );
    }

    @PostMapping("/create-unscheduled")
    public ResponseEntity<ManagerVisitDto> createUnscheduledVisit(
            @RequestBody @Valid CreateUnscheduledManagerVisitRequest request
    ) {
        return ResponseEntity.ok(
                managerVisitService.createAndMarkUnscheduledVisit(request)
        );
    }

    @GetMapping("/get-manager-compliance-record")
    public ResponseEntity<VisitComplianceResponse> getManagerVisitCompliance(
            @RequestParam("managerId") Long managerId,
            @RequestParam(value = "week", defaultValue = "all") String week) {

        VisitComplianceResponse response =
                managerVisitService.getManagerVisitCompliance(managerId, week);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/get-all-visits-by-week-day")
    public ResponseEntity<ApiResponseDto<List<CompletedVisitDto>>> fetchVisitsByWeekAndDay(
            @RequestParam Long managerId,
            @RequestParam Integer weekNumber,
            @RequestParam Integer dayOfWeek
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                managerVisitService.getVisitsForManagerWeekAndDay(managerId, weekNumber, dayOfWeek),
                "Visits fetched successfully"
        ));
    }

    @PostMapping("/get-current-month-visits")
    public ResponseEntity<ApiResponseDto<List<CompletedVisitDto>>> getCurrentMonthVisitsForManager(
            @RequestParam Long managerId,
            @RequestParam Integer weekNumber,
            @RequestParam Integer dayOfWeek
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                managerVisitService.getCurrentMonthVisitsForManager(managerId, weekNumber, dayOfWeek),
                "Visits fetched successfully"
        ));
    }


    @PostMapping("/admin/mark-visit-as-completed/{visitId}")
    public ResponseEntity<ApiResponseDto<CompletedVisitDto>> markVisitAsCompleted(@PathVariable Long visitId) {
        CompletedVisitDto  updatedVisit = managerVisitService.markManagerVisitAsCompleted(visitId);
        return ResponseEntity.ok(ApiResponseDto.success(updatedVisit, "Visit Marked as Completed"));
    }

    @GetMapping("/reports")
    public ResponseEntity<VisitReportDto> getManagerVisitSummary(
            @RequestParam Long managerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam String status,
            @RequestParam String category,
            @RequestParam String docType
    ) {
        return ResponseEntity.ok(
                managerVisitService.getManagerVisitReport(managerId, from, to, status, category,docType)
        );
    }


    /* ============================================================
   Manager-side: request a new FE
   ============================================================ */
    @PostMapping("/request-new-fe")
    public ResponseEntity<ApiResponseDto<ManagerFeRequestResponseDto>> requestNewFe(
            @RequestBody @Valid RequestNewManagerFeDto dto
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                managerVisitService.requestNewFieldExecutive(dto),
                "Request submitted successfully"));
    }

    @GetMapping("/fe-requests")
    public ResponseEntity<ApiResponseDto<List<ManagerFeRequestResponseDto>>> getMyFeRequests(
            @RequestParam Long managerId
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                managerVisitService.getRequestsForManager(managerId),
                "Requests fetched successfully"));
    }

    @PostMapping("/fe-requests/cancel/{requestId}")
    public ResponseEntity<ApiResponseDto<ManagerFeRequestResponseDto>> cancelFeRequest(
            @PathVariable Long requestId,
            @RequestParam Long managerId
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                managerVisitService.cancelRequest(requestId, managerId),
                "Request cancelled"));
    }

    /* ============================================================
       Admin-side: review pending manager FE requests
       ============================================================ */
    @GetMapping("/admin/fe-requests/pending")
    public ResponseEntity<ApiResponseDto<List<ManagerFeRequestResponseDto>>> getPendingFeRequests() {
        return ResponseEntity.ok(ApiResponseDto.success(
                managerVisitService.getAllPendingRequests(),
                "Pending requests fetched"));
    }

    @PostMapping("/admin/fe-requests/approve")
    public ResponseEntity<ApiResponseDto<ManagerFeRequestResponseDto>> approveFeRequest(
            @RequestBody @Valid ApproveRejectFeRequestDto dto
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                managerVisitService.approveRequest(dto),
                "Request approved and visits re-assigned"));
    }

    @PostMapping("/admin/fe-requests/reject")
    public ResponseEntity<ApiResponseDto<ManagerFeRequestResponseDto>> rejectFeRequest(
            @RequestBody @Valid ApproveRejectFeRequestDto dto
    ) {
        return ResponseEntity.ok(ApiResponseDto.success(
                managerVisitService.rejectRequest(dto),
                "Request rejected"));
    }


}
