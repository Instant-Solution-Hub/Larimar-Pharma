package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Doctor;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorDetailsDto {

    private Long id;
    private String name;
    private Doctor.Category category;
    private Doctor.PracticeType practiceType;
    private String designation;
    private String hospitalName;
    private String location;
    private String contactNumber;
    private String doctorCode;
    private String latitude;
    private String longitude;
    private boolean active;
}
