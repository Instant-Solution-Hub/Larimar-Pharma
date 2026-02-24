package com.instantsolutions.larimarpharma.DTOs;

public interface VisitSummaryProjection {
    Long getCompletedVisitCount();
    Long getMissedVisitCount();
    Long getCompletedDoctorVisitCount();
    Long getCompletedPharmacistVisitCount();
    Long getMissedDoctorVisitCount();
    Long getMissedPharmacistVisitCount();
    Long getCompletedAPlusVisits();
    Long getMissedAPlusVisits();
    Long getCompletedAVisits();
    Long getMissedAVisits();
    Long getCompletedBVisits();
    Long getMissedBVisits();
}