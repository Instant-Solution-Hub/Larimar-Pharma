package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.StockistProductStockRequestDto;
import com.instantsolutions.larimarpharma.DTOs.StockistProductStockResponseDto;
import com.instantsolutions.larimarpharma.service.StockistProductStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stockist-stocks")
@RequiredArgsConstructor
public class StockistProductStockController {

    private final StockistProductStockService stockService;


    /*
     * ADD
     *
     * POST /api/stockist-stocks/fe/{feId}
     */
    @PostMapping("/fe/{feId}")
    public ResponseEntity<StockistProductStockResponseDto> addStock(
            @PathVariable Long feId,
            @Valid @RequestBody StockistProductStockRequestDto dto
    ) {

        return ResponseEntity.ok(
                stockService.addStock(feId, dto)
        );
    }


    /*
     * UPDATE QUANTITY
     *
     * PUT /api/stockist-stocks/fe/{feId}/{stockId}
     */
    @PutMapping("/fe/{feId}/{stockId}")
    public ResponseEntity<StockistProductStockResponseDto> updateStock(
            @PathVariable Long feId,
            @PathVariable Long stockId,
           @Valid @RequestBody StockistProductStockRequestDto dto
    ) {

        return ResponseEntity.ok(
                stockService.updateStock(
                        feId,
                        stockId,
                        dto
                )
        );
    }


    /*
     * DELETE
     *
     * DELETE /api/stockist-stocks/fe/{feId}/{stockId}
     */
    @DeleteMapping("/fe/{feId}/{stockId}")
    public ResponseEntity<Void> deleteStock(
            @PathVariable Long feId,
            @PathVariable Long stockId
    ) {

        stockService.deleteStock(
                feId,
                stockId
        );

        return ResponseEntity.noContent().build();
    }


    /*
     * GET CURRENT MONTH
     *
     * FE + PRODUCT
     *
     * GET /api/stockist-stocks/fe/5/product/25/current-month
     */
    @GetMapping("/fe/{feId}/product/{productId}/current-month")
    public ResponseEntity<List<StockistProductStockResponseDto>>
    getCurrentMonthStockByFeAndProduct(
            @PathVariable Long feId,
            @PathVariable Long productId
    ) {

        return ResponseEntity.ok(
                stockService.getCurrentMonthStockByFeAndProduct(
                        feId,
                        productId
                )
        );
    }


    /*
     * GET CURRENT MONTH
     *
     * FE + ALL PRODUCTS + ALL STOCKISTS
     *
     * GET /api/stockist-stocks/fe/5/current-month
     */
    @GetMapping("/fe/{feId}/current-month")
    public ResponseEntity<List<StockistProductStockResponseDto>>
    getCurrentMonthStockByFe(
            @PathVariable Long feId
    ) {

        return ResponseEntity.ok(
                stockService.getCurrentMonthStockByFe(feId)
        );
    }


    /*
     * GET CURRENT MONTH
     *
     * FE + STOCKIST
     *
     * GET /api/stockist-stocks/fe/5/stockist/10/current-month
     */
    @GetMapping("/fe/{feId}/stockist/{stockistId}/current-month")
    public ResponseEntity<List<StockistProductStockResponseDto>>
    getCurrentMonthStockByFeAndStockist(
            @PathVariable Long feId,
            @PathVariable Long stockistId
    ) {

        return ResponseEntity.ok(
                stockService.getCurrentMonthStockByFeAndStockist(
                        feId,
                        stockistId
                )
        );
    }
}