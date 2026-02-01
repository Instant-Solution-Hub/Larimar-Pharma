package com.instantsolutions.larimarpharma.DTOs;

import lombok.Data;

@Data
public class ManagerContactUpdateRequestDto {

    private String phone;
    private String email;
    private String emergencyContact;
}
