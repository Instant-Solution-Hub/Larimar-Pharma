package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.WorkApprovalRequestDto;
import com.instantsolutions.larimarpharma.DTOs.WorkApprovalResponseDto;
import com.instantsolutions.larimarpharma.service.WorkApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam Long adminId,
            @RequestParam String remarks) {
        return ResponseEntity.ok(service.rejectRequest(id, adminId, remarks));
    }
}

