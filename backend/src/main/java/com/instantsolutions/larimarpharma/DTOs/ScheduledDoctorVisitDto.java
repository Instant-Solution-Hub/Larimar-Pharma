package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Doctor;
import com.instantsolutions.larimarpharma.entity.Visit;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduledDoctorVisitDto {

    private Long visitId;

    private Long doctorId;
    private String doctorName;
    private Doctor.Category category;
    private Doctor.PracticeType practiceType;
    private String hospitalName;
    private String location;
    private String contactNumber;

    private Visit.VisitStatus status;
    private Integer weekNumber;
    private Integer dayOfWeek;
}
