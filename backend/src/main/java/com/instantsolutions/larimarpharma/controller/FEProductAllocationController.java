package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.service.FEProductAllocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/field-executives")
@RequiredArgsConstructor
public class FEProductAllocationController {

    private final FEProductAllocationService allocationService;

    /**
     * Used when user selects a product in the modal
     * → Auto-fills target liquidation quantity
     */
    @GetMapping("/{feId}/products/{productId}/stock")
    public ResponseEntity<ApiResponseDto<FEProductStockDto>> getProductStock(
            @PathVariable Long feId,
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        allocationService.getProductStockForFE(feId, productId),
                        "Product stock fetched successfully"
                )
        );
    }

    /**
     * Used to populate product dropdown with FE-specific products
     */
    @GetMapping("/{feId}/products")
    public ResponseEntity<ApiResponseDto<List<FEProductStockDto>>> getAllocatedProducts(
            @PathVariable Long feId
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        allocationService.getAllAllocatedProducts(feId),
                        "Allocated products fetched successfully"
                )
        );
    }

    @PostMapping("/allocate-product")
    public ResponseEntity<ApiResponseDto<FEProductAllocationResponseDto>>
    allocateProduct(
            @Valid @RequestBody FEProductAllocationRequestDto dto
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        allocationService.allocateProductToFE(dto),
                        "Product allocated successfully"
                )
        );
    }

    @PutMapping("/update-product-stock")
    public ResponseEntity<ApiResponseDto<FEProductAllocationResponseDto>>
    updateProductStock(
            @Valid @RequestBody UpdateFEProductStockRequestDto dto
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        allocationService.updateAllocatedProductStock(dto),
                        "Product stock updated successfully"
                )
        );
    }

}

