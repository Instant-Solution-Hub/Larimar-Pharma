package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.CompanyTargetRequestDto;
import com.instantsolutions.larimarpharma.entity.CompanyTarget;
import com.instantsolutions.larimarpharma.service.CompanyTargetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/super-admin/company-target")
@RequiredArgsConstructor
public class CompanyTargetController {

    private final CompanyTargetService service;

    @PostMapping
    public CompanyTarget addOrUpdateTarget(
            @RequestBody CompanyTargetRequestDto dto) {
        return service.addOrUpdateTarget(dto);
    }

    @GetMapping("/current-month")
    public CompanyTarget getCurrentMonthTarget() {
        return service.getCurrentMonthTarget();
    }

}

