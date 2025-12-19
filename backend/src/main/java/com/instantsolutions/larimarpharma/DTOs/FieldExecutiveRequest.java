package com.instantsolutions.larimarpharma.DTOs;

import lombok.Data;

@Data
public class FieldExecutiveRequest {

    private String name;
    private String email;
    private String phone;
    private String password;

    private String employeeCode;
    private String territory;
    private String region;

    private Long managerId;
}
