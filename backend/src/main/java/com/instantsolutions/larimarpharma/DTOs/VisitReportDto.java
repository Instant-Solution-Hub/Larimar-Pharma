package com.instantsolutions.larimarpharma.DTOs;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VisitReportDto {
    private long completedVisitCount;
    private long missedVisitCount;
    private long pendingVisitCount;
    private List<TodayScheduledVisitDto> visits;
}
