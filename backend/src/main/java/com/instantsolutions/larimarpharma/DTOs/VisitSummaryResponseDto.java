package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VisitSummaryResponseDto {

    // Overall
    private long completedVisitCount;
    private long missedVisitCount;

    // Type-wise
    private long completedDoctorVisitCount;
    private long completedPharmacistVisitCount;
    private long missedDoctorVisitCount;
    private long missedPharmacistVisitCount;

    // Category-wise (Doctor only)
    private long completedAPlusVisits;
    private long missedAPlusVisits;

    private long completedAVisits;
    private long missedAVisits;

    private long completedBVisits;
    private long missedBVisits;
}