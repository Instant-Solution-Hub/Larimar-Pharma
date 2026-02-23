package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyProductSalesSummaryDto {

    private String feName;
    private String productName;
    private Double pts;
    private Integer quantity;
    private Double sales;
}