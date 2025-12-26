package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConvertedProductRequestDto {

    @NotNull
    private Long productId;

    @NotNull
    private Integer quantity;

    private Double value; // optional (can auto-calc)
}
