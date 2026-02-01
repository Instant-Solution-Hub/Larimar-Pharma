package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuperAdminContactResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String emergencyNumber;

}
