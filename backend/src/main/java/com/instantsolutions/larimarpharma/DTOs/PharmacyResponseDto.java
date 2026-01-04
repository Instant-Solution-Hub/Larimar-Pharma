package com.instantsolutions.larimarpharma.DTOs;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PharmacyResponseDto {

    private Long id;
    private String pharmacyName;
    private String location;
    private String contactPerson;
    private String contactNumber;

    // doctor info (safe, no lazy issues)
    private Long doctorId;
    private String doctorName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
