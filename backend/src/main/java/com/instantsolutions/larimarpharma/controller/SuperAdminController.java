package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.AdminContactResponseDto;
import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.SuperAdminContactResponseDto;
import com.instantsolutions.larimarpharma.service.SuperAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/super-admin/contact")
@RequiredArgsConstructor
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<SuperAdminContactResponseDto>> getSuperAdminContact() {
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        superAdminService.getSuperAdminContact(),
                        "Contact details fetched successfully"
                )
        );
    }
}
