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





}
