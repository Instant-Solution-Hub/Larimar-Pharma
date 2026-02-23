package com.instantsolutions.larimarpharma.DTOs;

import lombok.Data;

@Data
public class UpdateStockistSalesDto {

    private Long feId;
    private Long stockistId;
    private Double price;
}