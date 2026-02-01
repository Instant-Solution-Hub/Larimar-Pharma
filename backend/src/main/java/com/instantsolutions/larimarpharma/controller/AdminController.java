package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.AdminContactResponseDto;
import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.ManagerContactResponseDto;
import com.instantsolutions.larimarpharma.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/contact")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<AdminContactResponseDto>> getAdminContact() {

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        adminService.getAdminContact(),
                        "Contact details fetched successfully"
                )
        );
    }
}
