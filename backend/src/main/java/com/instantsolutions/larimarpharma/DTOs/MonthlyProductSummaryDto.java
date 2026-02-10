package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyProductSummaryDto {

    private Long productId;
    private String productName;
    private String category;
    private String description;
    private Double price;
    private Double pts;
    private Double ptr;
    private Double totalSales;
}

