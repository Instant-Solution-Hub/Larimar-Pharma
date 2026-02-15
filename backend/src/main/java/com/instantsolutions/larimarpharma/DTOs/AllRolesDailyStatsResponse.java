package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AllRolesDailyStatsResponse {
    private int month;
    private int year;
    private List<DailyVisitStatsDto> fieldExecutiveStats;
    private List<DailyVisitStatsDto> managerStats;
}