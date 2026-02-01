package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.AssignManagerVisitRequest;
import com.instantsolutions.larimarpharma.DTOs.ManagerVisitDto;
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

    @GetMapping("/today-schedule/{id}")
    public ResponseEntity<ApiResponseDto<List<ManagerVisitDto>>> getTodaySchedule(@PathVariable Long id) {
        List<ManagerVisitDto> schedule = managerService.getTodaySchedule(id);
        return ResponseEntity.ok(ApiResponseDto.success(schedule, "Schedules fetched successfully"));
    }
}
