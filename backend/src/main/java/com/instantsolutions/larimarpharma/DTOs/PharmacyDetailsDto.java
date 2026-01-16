package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PharmacyDetailsDto {

    private Long id;
    private String pharmacyName;
    private String location;
    private String contactPerson;
    private String contactNumber;

    // Optional: linked doctor info
    private Long doctorId;
    private String doctorName;
}
