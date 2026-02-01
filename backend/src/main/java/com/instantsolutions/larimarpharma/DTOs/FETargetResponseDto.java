package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FETargetResponseDto {

    private Long feId;
    private String feName;
    private String territory;

    private Double primaryTarget;
    private Double secondaryTarget;

    private Integer month;
    private Integer year;
}

