package com.instantsolutions.larimarpharma.DTOs;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StockistProductStockResponseDto {

    private Long id;
    private Long stockistId;
    private String stockistName;
    private String marketName;

    private Long productId;
    private String productName;

    private Integer availableQuantity;
    private LocalDateTime updatedAt;
}
