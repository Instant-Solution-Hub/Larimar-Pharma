package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Visit;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduledPharmacyVisitDto {

    private Long visitId;

    private Long pharmacyId;
    private String pharmacyName;
    private String location;
    private String contactPerson;
    private String contactNumber;

    private Visit.VisitStatus status;
    private Integer weekNumber;
    private Integer dayOfWeek;
}

