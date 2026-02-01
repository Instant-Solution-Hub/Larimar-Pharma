package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FEMonthlyTargetResponseDto {

    private Long feId;
    private String feName;
    private String territory;

    private Double primaryTargetSet;
    private Double secondaryTargetSet;
}
