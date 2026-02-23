package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyStockistSalesSummaryDto {

    private String feName;
    private String feRegion;
    private String stockistName;
    private Double price;
}