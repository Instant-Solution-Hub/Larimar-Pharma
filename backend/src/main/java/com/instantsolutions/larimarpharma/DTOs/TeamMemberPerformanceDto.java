package com.instantsolutions.larimarpharma.DTOs;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberPerformanceDto {
    private String id;
    private String name;
    private int totalVisitsToday;
    private double targetAchieved;
}