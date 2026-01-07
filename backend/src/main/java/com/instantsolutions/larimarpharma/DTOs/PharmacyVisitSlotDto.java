package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Visit;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PharmacyVisitSlotDto {

    private Long visitId;

    private Long pharmacyId;
    private String pharmacyName;

    private Integer weekNumber;
    private Integer dayOfWeek;

    private Visit.VisitStatus status;
    private Visit.VisitType visitType;

    // UI counters
    private Long completedVisitCount;   // e.g. 1
    private Long plannedVisitCount;     // e.g. 2
}
