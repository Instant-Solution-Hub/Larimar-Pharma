package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

import java.util.List;

@Data
@Builder
public class MonthlyDoctorTargetProgressDto {

    private int totalTargetVisits;
    private int totalCompletedVisits;
    private int overallProgress; // %

    private List<CategoryProgressDto> categories;
}
