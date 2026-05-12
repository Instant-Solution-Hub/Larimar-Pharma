package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.ManagerRepository;
import com.instantsolutions.larimarpharma.repository.PortalUnlockRequestRepository;
import com.instantsolutions.larimarpharma.service.PortalLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/portal")
@RequiredArgsConstructor
public class PortalLockController {

    private final PortalLockService portalLockService;
    private final PortalUnlockRequestRepository portalUnlockRequestRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final ManagerRepository managerRepository;

    /**
     * Check if user's portal is locked
     */
    @PostMapping("/status")
    public ResponseEntity<Map<String, Object>> getPortalStatus(@Valid @RequestBody UserIdentityDto userIdentity) {
        boolean isLocked = portalLockService.isPortalLocked(userIdentity);

        Map<String, Object> response = new HashMap<>();
        response.put("isLocked", isLocked);
        response.put("userType", userIdentity.getUserType());
        response.put("userId", userIdentity.getUserId());

        return ResponseEntity.ok(response);
    }

    /**
     * Request portal unlock
     */
    @PostMapping("/request-unlock")
    public ResponseEntity<?> requestUnlock(
            @Valid @RequestBody UnlockRequestDto requestDto) {

        PortalUnlockRequest request = portalLockService.requestUnlock(
                requestDto.getUserId(),
                requestDto.getUserType(),
                requestDto.getReason()
        );

        return ResponseEntity.ok(Map.of(
                "message", "Unlock request submitted successfully",
                "requestId", request.getId(),
                "status", request.getStatus()
        ));
    }

    /**
     * Get my unlock request history
     */
//    @PostMapping("/my-unlock-requests")
//    public ResponseEntity<?> getMyUnlockRequests(@Valid @RequestBody UserIdentityDto userIdentity) {
//        List<PortalUnlockRequest> requests = portalLockService.getUserUnlockRequests(
//                userIdentity.getUserId(),
//                userIdentity.getUserType()
//        );
//        return ResponseEntity.ok(requests);
//    }

    /**
     * Admin: Get all pending unlock requests
     */
    @PostMapping("/admin/unlock-requests/pending")
    public ResponseEntity<?> getPendingUnlockRequests(@Valid @RequestBody AdminIdentityDto adminIdentity) {
        return ResponseEntity.ok(portalUnlockRequestRepository.findAllPendingRequests());
    }

    /**
     * Admin: Get pending requests by user type
     */
    @PostMapping("/admin/unlock-requests/pending/{userType}")
    public ResponseEntity<?> getPendingUnlockRequestsByType(
            @PathVariable String userType,
            @Valid @RequestBody AdminIdentityDto adminIdentity) {

        PortalUnlockRequest.UserType type = PortalUnlockRequest.UserType.valueOf(userType.toUpperCase());
        return ResponseEntity.ok(portalUnlockRequestRepository.findPendingRequestsByUserType(type));
    }

    /**
     * Admin: Process unlock request
     */
    @PostMapping("/admin/unlock-requests/{requestId}/process")
    public ResponseEntity<?> processUnlockRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody ProcessUnlockRequestDto processDto) {

        portalLockService.processUnlockRequest(
                requestId,
                processDto.isApprove(),
                processDto.getAdminId(),
                processDto.getComments()
        );

        return ResponseEntity.ok(Map.of(
                "message", "Unlock request processed successfully"
        ));
    }

    /**
     * Admin: Manually lock user portal
     */
    @PostMapping("/admin/manual-lock")
    public ResponseEntity<?> manuallyLockPortal(@Valid @RequestBody ManualLockRequestDto lockDto) {

        portalLockService.manuallyLockPortal(
                lockDto.getUserId(),
                lockDto.getUserType(),
                lockDto.getLockDate(),
                lockDto.getReason(),
                lockDto.getAdminId()
        );

        return ResponseEntity.ok(Map.of(
                "message", "Portal locked manually"
        ));
    }

    /**
     * Admin: Manually unlock user portal
     */
    @PostMapping("/admin/manual-unlock")
    public ResponseEntity<?> manuallyUnlockPortal(@Valid @RequestBody ManualLockRequestDto lockDto) {

        portalLockService.manuallyUnlockPortal(
                lockDto.getUserId(),
                lockDto.getUserType(),
                lockDto.getLockDate(),
                lockDto.getReason(),
                lockDto.getAdminId()
        );

        return ResponseEntity.ok(Map.of(
                "message", "Portal unlocked manually"
        ));
    }


    /**
     * Admin: get all unlock requests
     */
    @GetMapping("/admin/all-requests")
    public ResponseEntity<ApiResponseDto<List<PortalUnlockRequestDto>>> getAllUnlockRequests() {

        return ResponseEntity.ok(ApiResponseDto.success(portalLockService.getAllUnlockRequests(), ""));
    }
}