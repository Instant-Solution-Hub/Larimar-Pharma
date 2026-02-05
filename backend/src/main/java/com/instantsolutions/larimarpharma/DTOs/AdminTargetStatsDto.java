package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminTargetStatsDto{
    Double targetSet;
    Double targetAchieved;
    Long feCount;

}