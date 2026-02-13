package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.WorkApprovalRequestDto;
import com.instantsolutions.larimarpharma.DTOs.WorkApprovalResponseDto;
import com.instantsolutions.larimarpharma.service.WorkApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approval-requests")
@RequiredArgsConstructor
public class ApprovalRequestController {

    private final WorkApprovalService service;

    @PostMapping("/raise")
    public ResponseEntity<WorkApprovalResponseDto> raiseRequest(
            @RequestBody WorkApprovalRequestDto dto) {
        return ResponseEntity.ok(service.raiseRequest(dto));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<WorkApprovalResponseDto> approve(
            @PathVariable Long id,
            @RequestParam Long adminId) {
        return ResponseEntity.ok(service.approveRequest(id, adminId));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<WorkApprovalResponseDto> reject(
            @PathVariable Long id,
            @RequestParam Long adminId
            ) {
        return ResponseEntity.ok(service.rejectRequest(id, adminId));
    }

    @GetMapping("/total/current-month")
    public ResponseEntity<?> getCurrentMonthWorkApprovals() {
        return ResponseEntity.ok(service.getCurrentMonthWorkApprovals());
    }

    @GetMapping("/current-month")
    public ResponseEntity<List<WorkApprovalResponseDto>> getCurrentMonthApprovals(
            @RequestParam(required = false) Long fieldExecutiveId,
            @RequestParam(required = false) Long managerId
    ) {

        List<WorkApprovalResponseDto> response =
                service.getCurrentMonthWorkApprovals(fieldExecutiveId, managerId);

        return ResponseEntity.ok(response);
    }

}

