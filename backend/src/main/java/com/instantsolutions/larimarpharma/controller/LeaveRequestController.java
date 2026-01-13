package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.LeaveRequestDto;
import com.instantsolutions.larimarpharma.entity.LeaveRequest;
import com.instantsolutions.larimarpharma.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @PostMapping("/apply")
    public ResponseEntity<LeaveRequest> applyLeave( @Valid @RequestBody LeaveRequestDto dto) {
        LeaveRequest leaveRequest = leaveRequestService.applyLeave(dto);
        return ResponseEntity.ok(leaveRequest);
    }
    @GetMapping("/{feId}/summary/current-month")
    public ResponseEntity<ApiResponseDto<Integer>> getCurrentMonthLeavesTaken(
            @PathVariable Long feId
    ) {
        int totalLeaves = leaveRequestService.getConfirmedLeavesForCurrentMonth(feId);

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        totalLeaves,
                        "Current month confirmed leaves fetched successfully"
                )
        );
    }

    @GetMapping("/{feId}")
    public ResponseEntity<List<LeaveRequest>> getLeavesByFieldExecutive(
            @PathVariable Long feId
    ) {
        List<LeaveRequest> leaves = leaveRequestService.getLeavesByFieldExecutive(feId);
        return ResponseEntity.ok(leaves);
    }
}
