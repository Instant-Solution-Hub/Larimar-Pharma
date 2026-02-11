package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.service.ManagerService;
import com.instantsolutions.larimarpharma.service.ManagerVisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

//    @GetMapping("/today-schedule/{id}")
//    public ResponseEntity<ApiResponseDto<List<ManagerVisitDto>>> getTodaySchedule(@PathVariable Long id) {
//        List<ManagerVisitDto> schedule = managerService.getTodaySchedule(id);
//        return ResponseEntity.ok(ApiResponseDto.success(schedule, "Schedules fetched successfully"));
//    }

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

    @GetMapping("/today-scheduled/{managerId}")
    public ResponseEntity<List<TodayScheduledVisitDto>> getTodayScheduledVisits(@RequestParam Long managerId) {
        return ResponseEntity.ok(
                managerVisitService.getTodaysVisits(managerId)
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


}
