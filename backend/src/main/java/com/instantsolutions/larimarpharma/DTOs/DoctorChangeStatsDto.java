package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorChangeStatsDto {

    private Long feId;
    private String feName;
    private Integer upgraded;

    private Integer downgraded;

}