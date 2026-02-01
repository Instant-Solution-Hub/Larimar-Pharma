// DashboardStatsDTO.java
package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private int totalVisits;
    private double teamTargetProgress;
    private int totalMembers;
    private String trend; // "+3% from last week"
}