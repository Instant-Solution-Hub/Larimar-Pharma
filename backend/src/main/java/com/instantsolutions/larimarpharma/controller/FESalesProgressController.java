package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.FESalesProgressRequestDto;
import com.instantsolutions.larimarpharma.entity.FESalesProgress;
import com.instantsolutions.larimarpharma.service.FESalesProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/fe/sales-progress")
@RequiredArgsConstructor
public class FESalesProgressController {

    @Autowired
    FESalesProgressService service;

    /**
     * Create or Update monthly product sales
     */
    @PostMapping
    public ResponseEntity<FESalesProgress> saveOrUpdate(
            @RequestParam Long feId,
            @RequestBody FESalesProgressRequestDto request) {

        return ResponseEntity.ok(service.saveOrUpdate(feId, request));
    }

    /**
     * View monthly progress
     */
    @GetMapping
    public ResponseEntity<List<FESalesProgress>> getMonthlyProgress(
            @RequestParam Long feId,
            @RequestParam YearMonth month) {

        return ResponseEntity.ok(
                service.getMyMonthlyProgress(feId, month)
        );
    }
}
