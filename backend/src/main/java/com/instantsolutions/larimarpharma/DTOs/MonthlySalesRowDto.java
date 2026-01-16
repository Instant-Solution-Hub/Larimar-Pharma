package com.instantsolutions.larimarpharma.DTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlySalesRowDto {
    private Long productId;
    private String productName;
    private Double pts;
    private Integer quantity;   // default 0
    private Double sales;       // default 0
}
