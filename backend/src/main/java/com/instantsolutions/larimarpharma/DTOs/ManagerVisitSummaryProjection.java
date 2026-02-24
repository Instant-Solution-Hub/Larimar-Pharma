package com.instantsolutions.larimarpharma.DTOs;


public interface ManagerVisitSummaryProjection {
    Long getCompletedVisitCount();
    Long getMissedVisitCount();
    Long getCompletedDoctorVisitCount();
    Long getMissedDoctorVisitCount();
    Long getCompletedAPlusVisits();
    Long getMissedAPlusVisits();
    Long getCompletedAVisits();
    Long getMissedAVisits();
    Long getCompletedBVisits();
    Long getMissedBVisits();
}