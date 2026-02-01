package com.instantsolutions.larimarpharma.DTOs;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerTargetStatsDto{
        Double targetSet;
        Double targetAchieved;
        Long feCount;

}
