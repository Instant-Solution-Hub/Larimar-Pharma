package com.instantsolutions.larimarpharma.DTOs;

import lombok.Data;

@Data
public class UpdateSalesQtyDto {
    private Long feId;
    private Long productId;
    private Integer quantity;
}
