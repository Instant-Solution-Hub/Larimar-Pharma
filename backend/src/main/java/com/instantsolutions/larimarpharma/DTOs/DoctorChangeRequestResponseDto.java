package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorChangeRequestResponseDto {

    private Long id;

    private Long doctorId;

    private String doctorName;

    private Long fieldExecutiveId;

    private String fieldExecutiveName;

    private String currentPracticeType;

    private String requestedPracticeType;

    private String status;

    private LocalDateTime requestedAt;

    private LocalDateTime reviewedAt;

}