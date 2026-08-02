package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Visit;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodayScheduledVisitDto  {

    private Long visitId;
    private Visit.VisitType visitType;
    private LocalDate visitDate;
    private String status;
    private String latitude;
    private String longitude;
    private String locationMethod;
    private  String photoProofUrl;

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

    private Integer visitSequence;        // 1, 2, 3, etc.
    private String sequenceLabel;         // "1st", "2nd", "3rd"
    private Integer requiredVisits;       // 2 for A, 3 for A+
    private String visitProgress;         // "1/3", "2/3", "3/3"
    private boolean isMinimumMet;         // true if required visits completed
    private String requirementStatus;
}
