package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Doctor;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponseDto {

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

    /* ===== Static Mapper ===== */
    public static DoctorResponseDto fromEntity(Doctor doctor) {
        return DoctorResponseDto.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .category(doctor.getCategory())
                .practiceType(doctor.getPracticeType())
                .designation(doctor.getDesignation())
                .hospitalName(doctor.getHospitalName())
                .location(doctor.getLocation())
                .contactNumber(doctor.getContactNumber())
                .doctorCode(doctor.getDoctorCode())
                .latitude(doctor.getLatitude())
                .longitude(doctor.getLongitude())
                .active(doctor.isActive())
                .build();
    }
}

