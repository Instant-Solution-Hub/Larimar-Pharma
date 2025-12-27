package com.instantsolutions.larimarpharma.DTOs;

import lombok.Data;

import java.time.YearMonth;

@Data
public class FESalesProgressRequestDto {

    private Long productId;
    private YearMonth salesMonth;
    private Integer quantity;

    /**
     * Optional: if FE wants to override product price
     */
    private Double price;
}
