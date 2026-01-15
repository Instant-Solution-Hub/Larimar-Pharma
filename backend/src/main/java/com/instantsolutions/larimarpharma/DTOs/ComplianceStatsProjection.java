package com.instantsolutions.larimarpharma.DTOs;


public interface ComplianceStatsProjection {
    Long getTotal();
    Long getCompleted();
    Long getMissed();
    Long getDoctorCompleted();
    Long getPharmacistCompleted();
}
