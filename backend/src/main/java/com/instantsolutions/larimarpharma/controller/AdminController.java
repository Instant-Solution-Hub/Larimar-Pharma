package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/contact")
    public ResponseEntity<ApiResponseDto<AdminContactResponseDto>> getAdminContact() {

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        adminService.getAdminContact(),
                        "Contact details fetched successfully"
                )
        );
    }

    @GetMapping("/{adminId}/dashboard-stats")
    public ResponseEntity<ApiResponseDto<AdminDashboardStatsDto>> getAdminDashboardStats(
            @PathVariable Long adminId
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        adminService.getAdminDashboardStats(adminId),
                        "Admin dashboard statistics fetched successfully"
                )
        );
    }

    @PutMapping("/{adminId}/contact/basic")
    public ResponseEntity<ApiResponseDto<AdminContactResponseDto>> updateContactDetails(
            @PathVariable Long adminId,
            @RequestBody ManagerContactUpdateRequestDto dto
    ) {
        AdminContactResponseDto updated =
                adminService.updateContactDetails(adminId, dto);

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        updated,
                        "Contact details updated successfully"
                )
        );
    }

}
