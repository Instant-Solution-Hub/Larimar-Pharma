package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.ProductRequestDto;
import com.instantsolutions.larimarpharma.entity.Product;
import com.instantsolutions.larimarpharma.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<Product>> create(
            @Valid @RequestBody ProductRequestDto dto
    ) {
        Product saved = productService.createProduct(dto);
        return new ResponseEntity<>(
                ApiResponseDto.success(saved, "Product created successfully"),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Product>> update(
            @PathVariable Long id,
            @Valid  @RequestBody ProductRequestDto dto
    ) {
        Product updated = productService.updateProduct(id, dto);
        return ResponseEntity.ok(
                ApiResponseDto.success(updated, "Product updated successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Product>> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        productService.getProductById(id),
                        "Product fetched successfully"
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<Product>>> getAll() {
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        productService.getAllProducts(),
                        "Products fetched successfully"
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(
            @PathVariable Long id
    ) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(
                ApiResponseDto.success(null, "Product deleted successfully")
        );
    }
}
