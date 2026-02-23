package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.MonthlyStockistSalesRowDto;
import com.instantsolutions.larimarpharma.DTOs.MonthlyStockistSalesSummaryDto;
import com.instantsolutions.larimarpharma.DTOs.UpdateStockistSalesDto;
import com.instantsolutions.larimarpharma.service.FEMonthlyStockistSalesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fe/monthly-stockist-sales")
@RequiredArgsConstructor
public class FEMonthlyStockistSalesController {

    private final FEMonthlyStockistSalesService service;

    @GetMapping
    public List<MonthlyStockistSalesRowDto> getMonthlySales(
            @RequestParam Long feId
    ) {
        return service.getMonthlySales(feId);
    }

    @PutMapping
    public ResponseEntity<Void> updatePrice(
            @RequestBody @Valid UpdateStockistSalesDto dto
    ) {
        service.updatePrice(
                dto.getFeId(),
                dto.getStockistId(),
                dto.getPrice()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/summary")
    public ResponseEntity<List<MonthlyStockistSalesSummaryDto>> getAllFESummary(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(
                service.getAllFESummary(year, month)
        );
    }
}
