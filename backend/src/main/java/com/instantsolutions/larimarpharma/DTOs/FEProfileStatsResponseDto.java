package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FEProfileStatsResponseDto {

    private Double targetAchieved;
    private Double targetSet;

    private Integer casualLeaves;
    private Integer approvedCasualLeaves;

    private Integer sickLeaves;
    private Integer approvedSickLeaves;
}