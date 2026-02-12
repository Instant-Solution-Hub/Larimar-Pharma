package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.MonthlyProductSummaryDto;
import com.instantsolutions.larimarpharma.DTOs.MonthlySalesRowDto;
import com.instantsolutions.larimarpharma.DTOs.UpdateSalesQtyDto;
import com.instantsolutions.larimarpharma.service.FEMonthlyProductSalesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fe/monthly-sales")
@RequiredArgsConstructor
public class FEMonthlyProductSalesController {

    private final FEMonthlyProductSalesService service;

    @GetMapping
    public List<MonthlySalesRowDto> getMonthlySales(
            @RequestParam Long feId
    ) {
        return service.getMonthlySales(feId);
    }

    @PutMapping
    public ResponseEntity<Void> updateQty(
            @RequestBody @Valid UpdateSalesQtyDto dto
    ) {
        service.updateQuantity(
                dto.getFeId(),
                dto.getProductId(),
                dto.getQuantity()
        );
        return ResponseEntity.ok().build();
    }



        @GetMapping("/previous-month/product-summary")
        public ResponseEntity<List<MonthlyProductSummaryDto>> getPreviousMonthSummary() {
            return ResponseEntity.ok(
                    service.getPreviousMonthSalesSummary()
            );
        }

}



