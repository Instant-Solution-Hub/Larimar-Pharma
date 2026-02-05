package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockistProductStockRequestDto {

    @NotNull
    private Long managerId;

    @NotNull
    private Long stockistId;

    @NotNull
    private Long productId;

    @NotNull
    @Min(0)
    private Integer quantity;
}

