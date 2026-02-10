package com.instantsolutions.larimarpharma.DTOs;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FEMarketSalesDto {

    private Long fieldExecutiveId;
    private String fieldExecutiveName;
    private String market;
    private Double salesAmount;
}
