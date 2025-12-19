package com.instantsolutions.larimarpharma.DTOs;


import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerRequestDto {

    // BaseUser fields
    private String name;
    private String email;
    private String phone;
    private boolean active;

    // Manager-specific fields
    private String employeeCode;
    private String department;
    private String designation;

    // Simple collections only
    private Set<String> managedTerritories;
}
