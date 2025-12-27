package com.instantsolutions.larimarpharma.DTOs;

import lombok.Data;

@Data
public class StockUpdateRequestDto {
    private Long stockistId;
    private Long productId;
    private Integer availableQuantity;
}
