package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.PromotionRequestDto;
import com.instantsolutions.larimarpharma.entity.Promotion;
import com.instantsolutions.larimarpharma.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<Promotion>> create(
            @RequestBody PromotionRequestDto dto
    ) {
        Promotion saved = promotionService.createPromotion(dto);
        return new ResponseEntity<>(
                ApiResponseDto.success(saved, "Promotion created successfully"),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Promotion>> update(
            @PathVariable Long id,
            @RequestBody PromotionRequestDto dto
    ) {
        Promotion updated = promotionService.updatePromotion(id, dto);
        return ResponseEntity.ok(
                ApiResponseDto.success(updated, "Promotion updated successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Promotion>> getById(@PathVariable Long id) {
        Promotion promotion = promotionService.getPromotionById(id);
        return ResponseEntity.ok(
                ApiResponseDto.success(promotion, "Promotion fetched successfully")
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<Promotion>>> getAll() {
        List<Promotion> promotions = promotionService.getAllPromotions();
        return ResponseEntity.ok(
                ApiResponseDto.success(promotions, "Promotions fetched successfully")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.ok(
                ApiResponseDto.success(null, "Promotion deleted successfully")
        );
    }
}
