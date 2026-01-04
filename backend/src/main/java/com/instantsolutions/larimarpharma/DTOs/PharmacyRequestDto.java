package com.instantsolutions.larimarpharma.DTOs;


import lombok.Data;

@Data
public class PharmacyRequestDto {

    private String pharmacyName;
    private String location;
    private String contactPerson;
    private String contactNumber;

    // relation
    private Long doctorId;
}
