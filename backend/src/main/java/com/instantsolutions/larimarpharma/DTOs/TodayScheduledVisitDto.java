package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Visit;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodayScheduledVisitDto  {

    private Long visitId;
    private Visit.VisitType visitType;
    private LocalDate visitDate;
    private String status;

    // Doctor
    private Long doctorId;
    private String doctorName;
    private String designation;
    private String category;
    private String practiceType;
    private String hospital;

    // Pharmacy
    private Long pharmacyId;
    private String pharmacyName;
    private String contactPerson;
    private String contactNumber;

    // Field Executive
    private Long fieldExecutiveId;
    private String fieldExecutiveName;
}
