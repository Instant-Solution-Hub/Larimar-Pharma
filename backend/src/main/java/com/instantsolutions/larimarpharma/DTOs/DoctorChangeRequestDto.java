package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Doctor;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DoctorChangeRequestDto {

    @NotNull
    private Long doctorId;

    @NotNull
    private Doctor.PracticeType requestedPracticeType;

}