package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ManagerProfileStatsDto {

    // Targets (aggregated from FEs)
    private Double targetSet;
    private Double targetAchieved;

    // Leave data (manager’s own)
    private Integer casualLeaves;
    private Integer approvedCasualLeaves;
    private Integer sickLeaves;
    private Integer approvedSickLeaves;
}

