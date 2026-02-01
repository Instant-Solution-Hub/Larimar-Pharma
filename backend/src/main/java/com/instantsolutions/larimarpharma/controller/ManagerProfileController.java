package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.ManagerProfileStatsDto;
import com.instantsolutions.larimarpharma.service.ManagerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/managers")
@RequiredArgsConstructor
public class ManagerProfileController {

    private final ManagerProfileService managerProfileService;

    @GetMapping("/{managerId}/profile-stats")
    public ManagerProfileStatsDto getManagerProfileStats(
            @PathVariable Long managerId
    ) {
        return managerProfileService.getManagerProfileStats(managerId);
    }
}

