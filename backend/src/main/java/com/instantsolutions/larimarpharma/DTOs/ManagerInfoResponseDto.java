package com.instantsolutions.larimarpharma.DTOs;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerInfoResponseDto {
    private Long id;

    private String name;
    private String email;
    private String phone;

    private String employeeCode;
    private String department;
    private String designation;

    private Set<String> managedTerritories;

}
