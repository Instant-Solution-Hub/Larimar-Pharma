package com.instantsolutions.larimarpharma.DTOs;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyVisitStatsDto {
    private String day;
    private long completed;
    private long missed;
    private long pending;
}