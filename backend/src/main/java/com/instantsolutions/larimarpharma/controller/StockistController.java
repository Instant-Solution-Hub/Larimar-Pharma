package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.AssignFieldExecutivesDto;
import com.instantsolutions.larimarpharma.DTOs.AssignProductStockDto;
import com.instantsolutions.larimarpharma.DTOs.StockistRequestDto;
import com.instantsolutions.larimarpharma.DTOs.StockistResponseDto;
import com.instantsolutions.larimarpharma.service.StockistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stockists")
@RequiredArgsConstructor
public class StockistController {

    private final StockistService stockistService;

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

    @PostMapping("/{id}/field-executives")
    public ResponseEntity<Void> assignFieldExecutives(
            @PathVariable Long id,
            @RequestBody AssignFieldExecutivesDto dto) {

        stockistService.assignFieldExecutives(id, dto.getFieldExecutiveIds());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/field-executives/{feId}")
    public ResponseEntity<Void> removeFieldExecutive(
            @PathVariable Long id,
            @PathVariable Long feId) {

        stockistService.removeFieldExecutive(id, feId);
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


}
