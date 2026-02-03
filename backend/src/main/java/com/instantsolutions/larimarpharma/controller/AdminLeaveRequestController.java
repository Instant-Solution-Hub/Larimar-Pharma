package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.AdminLeaveRequestDto;
import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.entity.LeaveRequest;
import com.instantsolutions.larimarpharma.service.AdminLeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/leaves")
public class AdminLeaveRequestController {

    @Autowired
    private AdminLeaveRequestService adminLeaveRequestService;

    /* ================= APPLY LEAVE ================= */

    @PostMapping("/apply")
    public ResponseEntity<ApiResponseDto<LeaveRequest>> applyLeave(
            @RequestBody AdminLeaveRequestDto dto
    ) {
        LeaveRequest leave = adminLeaveRequestService.applyLeave(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(
                        leave,
                        "Leave applied successfully"
                ));
    }

    /* ================= APPROVE LEAVE ================= */

//    @PutMapping("/{leaveId}/approve")
//    public ResponseEntity<ApiResponseDto<LeaveRequest>> approveLeave(
//            @PathVariable Long leaveId,
//            @RequestParam Long adminId
//    ) {
//        LeaveRequest leave =
//                adminLeaveRequestService.approveLeave(leaveId, adminId);
//
//        return ResponseEntity.ok(
//                ApiResponseDto.success(
//                        leave,
//                        "Leave approved successfully"
//                )
//        );
//    }

    /* ================= GET LEAVES BY ADMIN ================= */

    @GetMapping("/{adminId}")
    public ResponseEntity<ApiResponseDto<List<LeaveRequest>>> getLeavesByAdmin(
            @PathVariable Long adminId
    ) {
        List<LeaveRequest> leaves =
                adminLeaveRequestService.getLeavesByAdmin(adminId);

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        leaves,
                        "Leaves fetched successfully"
                )
        );
    }

    /* ================= CURRENT MONTH CONFIRMED LEAVES ================= */

    @GetMapping("/{adminId}/current-month/count")
    public ResponseEntity<ApiResponseDto<Integer>> getConfirmedLeavesForCurrentMonth(
            @PathVariable Long adminId
    ) {
        int days =
                adminLeaveRequestService.getConfirmedLeavesForCurrentMonth(adminId);

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        days,
                        "Confirmed leave days for current month fetched successfully"
                )
        );
    }
}
