package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZsmResponseDto {
    private Long id;

    private String name;
    private String email;
    private String phone;

    private String employeeCode;
    private String department;
}
