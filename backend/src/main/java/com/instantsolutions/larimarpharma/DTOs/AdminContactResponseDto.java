package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminContactResponseDto {
    private Long adminId;
    private String name;
    private String email;
    private String phone;
    private String emergencyContact;
}

