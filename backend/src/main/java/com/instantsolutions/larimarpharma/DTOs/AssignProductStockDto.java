package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignProductStockDto {

    @NotNull
    private Long productId;

    @Min(0)
    private Integer quantity;
}
