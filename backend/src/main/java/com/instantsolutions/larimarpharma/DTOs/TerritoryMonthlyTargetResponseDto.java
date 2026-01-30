package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TerritoryMonthlyTargetResponseDto {

    private Long id;

    private String territory;

    private Double primaryTargetSet;
    private Double secondaryTargetSet;

    private Double primaryTargetAchieved;
    private Double secondaryTargetAchieved;

    private Double primaryDeficit;
    private Double secondaryDeficit;

    private Integer subStockistStock;
}

