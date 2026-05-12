package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.service.FEMarketSalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fe/market-sales")
@RequiredArgsConstructor
public class FEMarketSalesController {

    private final FEMarketSalesService service;

    @GetMapping("/{feId}/current-month")
    public List<MarketSalesDto> getCurrentMonthSales(
            @PathVariable Long feId
    ) {
        return service.getCurrentMonthMarketSales(feId);
    }


    @GetMapping("/all-details")
    public ResponseEntity<List<MarketSalesDetailDto>> getSalesDetailForAllFEs(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(
                service.getMarketSalesDetail(year, month)
        );
    }
    @PutMapping("/{feId}")
    public MarketSalesDto updateMarketSales(
            @PathVariable Long feId,
            @RequestBody UpdateMarketSalesRequestDto request
    ) {
        return service.updateMarketSales(feId, request);
    }


      @GetMapping("/current-month/all")
        public ResponseEntity<List<MarketSalesSummaryDto>> getCurrentMonthSalesForAllFEs() {
            return ResponseEntity.ok(
                    service.getCurrentMonthMarketSalesForAllFEs()
            );
        }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<MarketSalesDetailDto>> getSalesDetailForManagerFEs(
            @PathVariable Long managerId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(
                service.getMarketSalesDetailByManager(managerId, year, month)
        );
    }
    }


