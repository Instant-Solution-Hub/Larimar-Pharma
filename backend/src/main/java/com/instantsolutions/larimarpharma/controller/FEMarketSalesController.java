package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.FEMarketSalesDto;
import com.instantsolutions.larimarpharma.DTOs.MarketSalesDto;
import com.instantsolutions.larimarpharma.DTOs.UpdateMarketSalesRequestDto;
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

    @PutMapping("/{feId}")
    public MarketSalesDto updateMarketSales(
            @PathVariable Long feId,
            @RequestBody UpdateMarketSalesRequestDto request
    ) {
        return service.updateMarketSales(feId, request);
    }


      @GetMapping("/current-month/all")
        public ResponseEntity<List<FEMarketSalesDto>> getCurrentMonthSalesForAllFEs() {
            return ResponseEntity.ok(
                    service.getCurrentMonthMarketSalesForAllFEs()
            );
        }
    }


