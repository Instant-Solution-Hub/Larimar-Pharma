package com.instantsolutions.larimarpharma.DTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlyOrderStatsDto {
    private Long fieldExecutiveId;
    private int year;
    private int month;

    private long totalOrders;
    private long pendingOrders;
    private Double totalSales;
}

