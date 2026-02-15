package com.instantsolutions.larimarpharma.DTOs;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyVisitStatsDto {
    private String week; // Will be "Week 1", "Week 2", etc.
    private long completed;
    private long missed;
    private long pending;
}