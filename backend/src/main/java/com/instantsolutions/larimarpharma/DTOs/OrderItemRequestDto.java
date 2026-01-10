package com.instantsolutions.larimarpharma.DTOs;

import lombok.Data;

import jakarta.validation.constraints.*;

@Data
public class OrderItemRequestDto {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private Double price;

    @PositiveOrZero(message = "Total must not be negative")
    private Double total;
}

