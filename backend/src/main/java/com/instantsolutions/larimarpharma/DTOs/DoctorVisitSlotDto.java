package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Visit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorVisitSlotDto {

    private Long visitId;

    private Long doctorId;
    private String doctorName;
    private String specialization;
    private String hospitalName;

    private Integer weekNumber;
    private Integer dayOfWeek;

    private Visit.VisitStatus status;
    private Visit.VisitType visitType;
    private String practiceType;
    private String category;

    // UI counters
    private Long completedVisitCount;   // e.g. 3
    private Long plannedVisitCount;     // e.g. 3
}
