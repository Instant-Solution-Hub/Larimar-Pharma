package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.service.DoctorChangeRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor-change-requests")
@RequiredArgsConstructor
public class DoctorChangeRequestController {

    private final DoctorChangeRequestService doctorChangeRequestService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<DoctorChangeRequestResponseDto>>>
    getAllRequests() {

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        doctorChangeRequestService.getAllRequests(),
                        "Doctor change requests fetched successfully"
                )
        );
    }

    @PutMapping("/{requestId}")
    public ResponseEntity<ApiResponseDto<DoctorChangeRequestResponseDto>>
    reviewRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody DoctorChangeReviewRequestDto dto
    ) {

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        doctorChangeRequestService.reviewRequest(
                                requestId,
                                dto
                        ),
                        "Doctor change request reviewed successfully"
                )
        );
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponseDto<List<DoctorChangeRequestResponseDto>>>
    getPendingRequests() {

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        doctorChangeRequestService.getAllPendingRequests(),
                        "Pending doctor change requests fetched successfully"
                )
        );
    }
}