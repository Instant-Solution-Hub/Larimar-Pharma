package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockistProductStockRequestDto {

    @NotNull(message = "Stockist ID is required")
    @Min(value = 0, message = "Stockist ID must be greater than or equal to 0")
    private Long stockistId;

    @NotNull(message = "Product ID is required")
    @Min(value = 0, message = "Product ID must be greater than or equal to 0")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}