package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockistProductStockResponseDto {

    private Long id;

    private Long fieldExecutiveId;

    private Long stockistId;
    private String stockistName;

    private Long productId;
    private String productName;

    private Integer quantity;

    private String month;
}