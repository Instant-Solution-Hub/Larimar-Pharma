package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ManagerVisitSummaryResponseDto {

    private long completedVisitCount;
    private long missedVisitCount;

    private long completedDoctorVisitCount;
    private long completedPharmacistVisitCount;

    private long missedDoctorVisitCount;
    private long missedPharmacistVisitCount;
}