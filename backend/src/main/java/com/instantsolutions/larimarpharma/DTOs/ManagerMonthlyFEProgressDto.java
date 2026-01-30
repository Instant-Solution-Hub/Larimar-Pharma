package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ManagerMonthlyFEProgressDto {

    private Long managerId;
    private Integer month;
    private Integer year;

    private Double totalTargetSet;
    private Double totalTargetAchieved;
    private Double progressPercentage;

    private Integer totalFieldExecutives;
}

