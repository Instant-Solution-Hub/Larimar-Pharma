package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarketSalesDto {
    private String market;
    private Double salesAmount;
}
