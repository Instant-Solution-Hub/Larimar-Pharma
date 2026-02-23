package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyStockistSalesRowDto {

    private Long stockistId;
    private String stockistName;
    private Double price;
}