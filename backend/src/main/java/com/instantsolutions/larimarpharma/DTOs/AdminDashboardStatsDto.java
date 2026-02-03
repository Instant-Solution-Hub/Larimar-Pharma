package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDashboardStatsDto {

    private Double targetSet;
    private Double targetAchieved;

    // Leave data (admin profile)
    private Integer casualLeaves;
    private Integer approvedCasualLeaves;
    private Integer sickLeaves;
    private Integer approvedSickLeaves;
}
