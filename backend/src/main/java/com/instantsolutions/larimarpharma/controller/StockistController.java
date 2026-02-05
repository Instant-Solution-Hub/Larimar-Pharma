package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.service.StockistProductStockService;
import com.instantsolutions.larimarpharma.service.StockistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stockists")
@RequiredArgsConstructor
public class StockistController {

    private final StockistService stockistService;
    private final StockistProductStockService stockService;

    @PostMapping
    public ResponseEntity<StockistResponseDto> create(@Valid @RequestBody StockistRequestDto dto) {
        return ResponseEntity.ok(stockistService.createStockist(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockistResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody StockistRequestDto dto) {
        return ResponseEntity.ok(stockistService.updateStockist(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockistResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(stockistService.getStockistById(id));
    }

    @GetMapping
    public ResponseEntity<List<StockistResponseDto>> getAll() {
        return ResponseEntity.ok(stockistService.getAllStockists());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockistService.deleteStockist(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/products")
    public ResponseEntity<Void> addProductStock(
            @PathVariable Long id,
            @RequestBody AssignProductStockDto dto) {

        stockistService.addOrUpdateProductStock(
                id, dto.getProductId(), dto.getQuantity());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/products/{productId}")
    public ResponseEntity<Void> removeProduct(
            @PathVariable Long id,
            @PathVariable Long productId) {

        stockistService.removeProductFromStockist(id, productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/add-stock")
    public ResponseEntity<ApiResponseDto<StockistProductStockResponseDto>> addStock(
            @RequestBody @Valid StockistProductStockRequestDto dto
    ) {
        return new ResponseEntity<>(
                ApiResponseDto.success(stockService.addStock(dto), "Stock added successfully"),
                HttpStatus.CREATED
        );
    }

    // 🔁 Update stock
    @PutMapping("/update-stock")
    public ResponseEntity<ApiResponseDto<StockistProductStockResponseDto>> updateStock(
            @RequestBody @Valid StockistProductStockRequestDto dto
    ) {

        return new ResponseEntity<>(
                ApiResponseDto.success(stockService.updateStock(dto), "Stock updated successfully"),
                HttpStatus.ACCEPTED
        );
    }

    // ❌ Delete stock
    @DeleteMapping("/delete-stock")
    public ResponseEntity<ApiResponseDto<Void>> deleteStock(
            @RequestParam Long managerId,
            @RequestParam Long stockistId,
            @RequestParam Long productId
    ) {
        stockService.deleteStock(managerId, stockistId, productId);

        return new ResponseEntity<>(
                ApiResponseDto.success(null, "Stock deleted successfully"),
                HttpStatus.ACCEPTED
        );
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<ApiResponseDto<List<ManagerStockistResponseDto>>>
    getStockistsByManager(@PathVariable Long managerId) {

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        stockistService.getStockistsByManager(managerId),
                        "Stockists fetched successfully"
                )
        );
    }

    @GetMapping("/manager/{managerId}/stocks")
    public ResponseEntity<ApiResponseDto<List<StockistProductStockResponseDto>>>
    getAllStocksByManager(
            @PathVariable Long managerId
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        stockService.getAllStocksUnderManager(managerId),
                        "Stocks fetched successfully"
                )
        );
    }


}
