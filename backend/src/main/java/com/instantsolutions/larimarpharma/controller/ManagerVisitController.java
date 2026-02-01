package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.AssignManagerVisitRequest;
import com.instantsolutions.larimarpharma.service.ManagerVisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manager-visits")
@RequiredArgsConstructor
public class ManagerVisitController {

    private final ManagerVisitService managerVisitService;

    @PostMapping("/assign")
    public ResponseEntity<ApiResponseDto<String>> assignManager(
            @RequestBody @Valid AssignManagerVisitRequest request
    ) {
        managerVisitService.assignManagerToVisit(request);
        return ResponseEntity.ok(ApiResponseDto.success(null,"Visits assigned successfully"));
    }
}
