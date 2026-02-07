package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TerritoryOverviewResponseDto {
    private Long id;

    private String territory;
    private String managerName;

    private Double primaryTarget;
    private Double secondaryTarget;

    private Double weeklyPrimarySale;
    private Double weeklySecondarySale;

    private Integer totalSubstockistStock;
}

