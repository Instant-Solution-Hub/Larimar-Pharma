package com.instantsolutions.larimarpharma.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMarketSalesRequestDto {
    private String market;
    private Double salesAmount;
}
