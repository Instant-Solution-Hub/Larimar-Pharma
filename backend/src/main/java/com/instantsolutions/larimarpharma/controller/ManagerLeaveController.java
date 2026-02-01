package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.ManagerLeaveRequestDto;
import com.instantsolutions.larimarpharma.entity.LeaveRequest;
import com.instantsolutions.larimarpharma.service.ManagerLeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/managers/leaves")
public class ManagerLeaveController {

    @Autowired
    private ManagerLeaveRequestService managerLeaveRequestService;

    /**
     * Apply for leave (Manager)
     */
    @PostMapping("/apply")
    public ResponseEntity<LeaveRequest> applyLeave(
            @RequestBody ManagerLeaveRequestDto dto
    ) {
        LeaveRequest leave = managerLeaveRequestService.applyLeave(dto);
        return ResponseEntity.ok(leave);
    }

    /**
     * Get all leave requests of logged-in manager
     */
    @GetMapping("/{managerId}")
    public ResponseEntity<List<LeaveRequest>> getManagerLeaves(
            @PathVariable Long managerId
    ) {
        List<LeaveRequest> leaves =
                managerLeaveRequestService.getLeavesByManager(managerId);
        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/{managerId}/confirmed-leaves/current-month")
    public ResponseEntity<Integer> getConfirmedLeavesForCurrentMonth(
            @PathVariable Long managerId
    ) {
        int days =
                managerLeaveRequestService
                        .getConfirmedLeavesForCurrentMonth(managerId);

        return ResponseEntity.ok(days);
    }

}
