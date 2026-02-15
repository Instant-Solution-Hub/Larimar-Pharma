package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllRolesWeeklyStatsResponse {
    private int month;
    private int year;
    private List<WeeklyVisitStatsDto> fieldExecutiveStats;
    private List<WeeklyVisitStatsDto> managerStats;


}