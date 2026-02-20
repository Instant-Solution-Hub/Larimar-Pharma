package com.instantsolutions.larimarpharma.DTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorVisitProgressDto {

    private Long doctorId;
    private String doctorName;
    private String category;
    private int requiredVisits;
    private int plannedVisits;
    private String progress; // "2/3"
}
