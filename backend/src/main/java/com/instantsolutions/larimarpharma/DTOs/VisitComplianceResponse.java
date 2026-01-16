package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitComplianceResponse {
    private ComplianceStatsDto stats;
    private List<ComplianceRecordDto> records;
    private Integer totalWeeks;
}
