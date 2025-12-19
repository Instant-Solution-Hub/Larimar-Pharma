package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyDoctorVisitStatDto {

    private Long fieldExecutiveId;
    private int year;
    private int month;

    private long totalDoctorVisits;
    private long scheduledDoctorVisits;
    private long completedDoctorVisits;
    private long missedDoctorVisits;
}

