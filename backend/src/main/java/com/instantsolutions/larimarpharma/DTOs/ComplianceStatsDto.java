package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceStatsDto {
    private Integer scheduled;
    private Integer completed;
    private Integer missed;
    private Integer complianceRate;
    private Integer doctorVisits;
    private Integer doctorCompleted;
    private Integer pharmacistVisits;
    private Integer pharmacistCompleted;
}
