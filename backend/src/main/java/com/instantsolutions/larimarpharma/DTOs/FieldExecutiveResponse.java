package com.instantsolutions.larimarpharma.DTOs;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldExecutiveResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;

    private String employeeCode;
    private String territory;
    private String region;

    private Long managerId;
}
