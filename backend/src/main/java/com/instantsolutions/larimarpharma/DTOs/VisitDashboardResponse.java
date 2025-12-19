package com.instantsolutions.larimarpharma.DTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VisitDashboardResponse {

    private long doctorVisits;
    private long pharmacyVisits;
    private long stockistVisit;

    private long totalDoctorVisitsForTheCurrentMonth;
    private String doctorTargetProgress;
}
