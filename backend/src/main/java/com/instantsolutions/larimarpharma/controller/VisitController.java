package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.VisitDashboardResponse;
import com.instantsolutions.larimarpharma.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visit")
public class VisitController {
    @Autowired
    VisitService visitService;

    @GetMapping("/dashboard/{feId}")
    public VisitDashboardResponse getVisitDashboard(@PathVariable Long feId) {
        return visitService.getDashboard(feId);
    }
}
