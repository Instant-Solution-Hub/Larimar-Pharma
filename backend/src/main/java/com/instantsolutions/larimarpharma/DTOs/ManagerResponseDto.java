package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.DTOs.FieldExecutiveResponse;
import lombok.*;

import java.util.Set;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerResponseDto {

    private Long id;

    private String name;
    private String email;
    private String phone;

    private String employeeCode;
    private String department;
    private String designation;

    private Set<String> managedTerritories;

    private List<FieldExecutiveResponse> fieldExecutives;
}
