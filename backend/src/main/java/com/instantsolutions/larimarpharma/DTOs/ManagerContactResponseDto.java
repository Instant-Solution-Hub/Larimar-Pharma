package com.instantsolutions.larimarpharma.DTOs;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ManagerContactResponseDto {

    private Long managerId;
    private String phone;
    private String email;
    private String emergencyContact;
    private String name;
}
