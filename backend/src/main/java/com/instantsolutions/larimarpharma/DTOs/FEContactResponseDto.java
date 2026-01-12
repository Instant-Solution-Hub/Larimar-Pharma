package com.instantsolutions.larimarpharma.DTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FEContactResponseDto {

    private Long feId;
    private String phone;
    private String email;
    private String emergencyContact;
    private String name;
}
