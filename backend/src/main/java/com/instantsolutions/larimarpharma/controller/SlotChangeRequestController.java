package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.entity.SlotChangeRequest;
import com.instantsolutions.larimarpharma.service.SlotChangeRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slot-change-requests")
@RequiredArgsConstructor
@Tag(name = "Slot Change Requests", description = "APIs for managing slot change requests")
public class SlotChangeRequestController {

    private final SlotChangeRequestService requestService;

    @PostMapping
    @Operation(summary = "Create a new slot change request")
    public ResponseEntity<SlotChangeRequestResponse> createRequest(
            @RequestBody SlotChangeRequestDto requestDTO) {

        SlotChangeRequestResponse request = requestService.createRequest(requestDTO, requestDTO.getUserId(), requestDTO.getUserType());
        return ResponseEntity.ok(request);
    }

    @GetMapping("/pending")
    @Operation(summary = "Get all pending requests (Admin only)")
    public ResponseEntity<List<SlotChangeRequestResponse>> getPendingRequests() {
        return ResponseEntity.ok(requestService.getPendingRequests());
    }

    @GetMapping("/my-requests")
    @Operation(summary = "Get my pending requests")
    public ResponseEntity<List<SlotChangeRequestResponse>> getMyRequests(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Type") String userType) {
        return ResponseEntity.ok(requestService.getUserRequests(userId, userType));
    }

    @GetMapping("/field-executive/{fieldExecutiveId}")
    @Operation(summary = "Get requests for a specific field executive")
    public ResponseEntity<List<SlotChangeRequestResponse>> getFieldExecutiveRequests(
            @PathVariable Long fieldExecutiveId) {
        return ResponseEntity.ok(requestService.getFieldExecutiveRequests(fieldExecutiveId));
    }

    @PostMapping("/{requestId}/{adminId}/review")
    @Operation(summary = "Admin review and approve/reject a request")
    public ResponseEntity<SlotChangeRequestResponse> reviewRequest(
            @PathVariable Long requestId,
            @RequestBody AdminReviewRequest reviewRequest,
            @PathVariable Long adminId) {

        SlotChangeRequestResponse request = requestService.processAdminReview(requestId, reviewRequest, adminId);
        return ResponseEntity.ok(request);
    }

    @PostMapping("/{requestId}/{userId}/{userType}/cancel")
    @Operation(summary = "Cancel a pending request")
    public ResponseEntity<Void> cancelRequest(
            @PathVariable Long requestId,
            @PathVariable("X-User-Id") Long userId,
            @PathVariable("X-User-Type") String userType) {

        requestService.cancelRequest(requestId, userId, userType);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/visit/{visitId}")
    @Operation(summary = "Get all requests for a specific visit")
    public ResponseEntity<List<SlotChangeRequestResponse>> getVisitRequests(
            @PathVariable Long visitId) {
        // You can implement this by adding a method in service
        // return ResponseEntity.ok(requestService.getRequestsByVisitId(visitId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/{adminId}/processed")
    @Operation(summary = "Get requests processed by an admin")
    public ResponseEntity<List<SlotChangeRequestResponse>> getAdminProcessedRequests(
            @PathVariable Long adminId) {
        // You can implement this by adding a method in service
        return ResponseEntity.ok().build();
    }
}