package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ManagerVisitSummaryResponseDto {

    // Overall
    private long completedVisitCount;
    private long missedVisitCount;

    // Doctor Type (Pharmacist skipped)
    private long completedDoctorVisitCount;
    private long missedDoctorVisitCount;

    // Category-wise
    private long completedAPlusVisits;
    private long missedAPlusVisits;

    private long completedAVisits;
    private long missedAVisits;

    private long completedBVisits;
    private long missedBVisits;
}