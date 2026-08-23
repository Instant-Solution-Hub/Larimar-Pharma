package com.instantsolutions.larimarpharma.controller;

// SlotPlanningDayRequestController.java

import com.instantsolutions.larimarpharma.DTOs.ApprovedSlotPlanRequestCheckDto;
import com.instantsolutions.larimarpharma.DTOs.SlotPlanningDayRequestDto;
import com.instantsolutions.larimarpharma.DTOs.SlotPlanningDayRequestResponseDto;
import com.instantsolutions.larimarpharma.DTOs.SlotPlanningDayRequestUpdateDto;
import com.instantsolutions.larimarpharma.service.SlotPlanningDayRequestService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/slot-planning-requests")
@RequiredArgsConstructor
public class SlotPlanningDayRequestController {

    private final SlotPlanningDayRequestService requestService;

    // Endpoint for Managers to create requests
    @PostMapping("/manager/{managerId}")
    public ResponseEntity<SlotPlanningDayRequestResponseDto> createRequestByManager(
            @PathVariable Long managerId,
            @Valid @RequestBody SlotPlanningDayRequestDto requestDto) {
        SlotPlanningDayRequestResponseDto createdRequest = requestService.createRequest(requestDto, "MANAGER", managerId);
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    // Endpoint for ZSM to create requests
    @PostMapping("/zsm/{zsmId}")
    public ResponseEntity<SlotPlanningDayRequestResponseDto> createRequestByZsm(
            @PathVariable Long zsmId,
            @Valid @RequestBody SlotPlanningDayRequestDto requestDto) {
        SlotPlanningDayRequestResponseDto createdRequest = requestService.createRequest(requestDto, "ZSM",zsmId);
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    // Endpoint for Field Executives to create requests
    @PostMapping("/field-executive/{feId}")
    public ResponseEntity<SlotPlanningDayRequestResponseDto> createRequestByFieldExecutive(
            @PathVariable Long feId,
            @Valid @RequestBody SlotPlanningDayRequestDto requestDto) {
        SlotPlanningDayRequestResponseDto createdRequest = requestService.createRequest(requestDto, "FE", feId);
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    // Admin endpoints for approving/rejecting requests
    @PutMapping("/{requestId}/review")
    public ResponseEntity<SlotPlanningDayRequestResponseDto> reviewRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody SlotPlanningDayRequestUpdateDto updateDto,
            @RequestParam Long adminId) {
        SlotPlanningDayRequestResponseDto updatedRequest = requestService.updateRequestStatus(requestId, updateDto, adminId);
        return ResponseEntity.ok(updatedRequest);
    }

    // Get single request
    @GetMapping("/{requestId}")
    public ResponseEntity<SlotPlanningDayRequestResponseDto> getRequestById(@PathVariable Long requestId) {
        SlotPlanningDayRequestResponseDto request = requestService.getRequestById(requestId);
        return ResponseEntity.ok(request);
    }

    // Get requests by Manager
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<SlotPlanningDayRequestResponseDto>> getRequestsByManager(@PathVariable Long managerId) {
        List<SlotPlanningDayRequestResponseDto> requests = requestService.getRequestsByManager(managerId);
        return ResponseEntity.ok(requests);
    }

    // Get requests by ZSM
    @GetMapping("/zsm/{zsmId}")
    public ResponseEntity<List<SlotPlanningDayRequestResponseDto>> getRequestsByZsm(@PathVariable Long zsmId) {
        List<SlotPlanningDayRequestResponseDto> requests = requestService.getRequestsByZsm(zsmId);
        return ResponseEntity.ok(requests);
    }

    // Get requests by Field Executive
    @GetMapping("/field-executive/{feId}")
    public ResponseEntity<List<SlotPlanningDayRequestResponseDto>> getRequestsByFieldExecutive(@PathVariable Long feId) {
        List<SlotPlanningDayRequestResponseDto> requests = requestService.getRequestsByFieldExecutive(feId);
        return ResponseEntity.ok(requests);
    }

    // Admin endpoints
    @GetMapping("/admin/all")
    public ResponseEntity<List<SlotPlanningDayRequestResponseDto>> getAllRequestsForAdmin() {
        List<SlotPlanningDayRequestResponseDto> requests = requestService.getAllRequestsForAdmin();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<List<SlotPlanningDayRequestResponseDto>> getPendingRequests() {
        List<SlotPlanningDayRequestResponseDto> requests = requestService.getPendingRequests();
        return ResponseEntity.ok(requests);
    }

    // Cancel request (for requesters)
    @PutMapping("/{requestId}/cancel")
    public ResponseEntity<Void> cancelRequest(
            @PathVariable Long requestId,
            @RequestParam Long requesterId,
            @RequestParam String requesterType) {
        requestService.cancelRequest(requestId, requesterId, requesterType);
        return ResponseEntity.noContent().build();
    }

    // Optional: Add endpoint to get requests by date range (if needed)
    @GetMapping("/admin/date-range")
    public ResponseEntity<List<SlotPlanningDayRequestResponseDto>> getRequestsByDateRange(
            @RequestParam String status,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        // You would need to add this method to the service if needed
        // List<SlotPlanningDayRequestResponseDto> requests = requestService.getRequestsByDateRange(status, startDate, endDate);
        // return ResponseEntity.ok(requests);
        return ResponseEntity.ok(null); // Placeholder
    }

    // Optional: Add endpoint to get requests by requester type
    @GetMapping("/requester/{requesterType}/{requesterId}")
    public ResponseEntity<List<SlotPlanningDayRequestResponseDto>> getRequestsByRequester(
            @PathVariable String requesterType,
            @PathVariable Long requesterId) {
        List<SlotPlanningDayRequestResponseDto> requests;
        if ("MANAGER".equalsIgnoreCase(requesterType)) {
            requests = requestService.getRequestsByManager(requesterId);
        } else if ("FE".equalsIgnoreCase(requesterType)) {
            requests = requestService.getRequestsByFieldExecutive(requesterId);
        } else {
            throw new RuntimeException("Invalid requester type");
        }
        return ResponseEntity.ok(requests);
    }


    // Check approved request for Manager today
    @GetMapping("/manager/{managerId}/check-approved-today")
    public ResponseEntity<ApprovedSlotPlanRequestCheckDto> checkApprovedRequestForManagerToday(
            @PathVariable Long managerId) {
        ApprovedSlotPlanRequestCheckDto result = requestService.checkApprovedRequestForManagerToday(managerId);
        return ResponseEntity.ok(result);
    }

    // Check approved request for Field Executive today
    @GetMapping("/field-executive/{feId}/check-approved-today")
    public ResponseEntity<ApprovedSlotPlanRequestCheckDto> checkApprovedRequestForFieldExecutiveToday(
            @PathVariable Long feId) {
        ApprovedSlotPlanRequestCheckDto result = requestService.checkApprovedRequestForFieldExecutiveToday(feId);
        return ResponseEntity.ok(result);
    }

    // Unified endpoint to check approved request for any requester today
    @GetMapping("/check-approved-today")
    public ResponseEntity<ApprovedSlotPlanRequestCheckDto> checkApprovedRequestToday(
            @RequestParam String requesterType,
            @RequestParam Long requesterId) {
        ApprovedSlotPlanRequestCheckDto result = requestService.checkApprovedRequestToday(requesterType, requesterId);
        return ResponseEntity.ok(result);
    }

    // Simple boolean check if user can plan slot today
    @GetMapping("/can-plan-slot-today")
    public ResponseEntity<Map<String, Boolean>> canPlanSlotToday(
            @RequestParam String requesterType,
            @RequestParam Long requesterId) {
        boolean canPlan = requestService.canPlanSlotToday(requesterType, requesterId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("canPlanSlot", canPlan);
        return ResponseEntity.ok(response);
    }

}