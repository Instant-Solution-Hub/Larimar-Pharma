package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignFETargetRequestDto {

    @NotNull
    private Integer month;

    @NotNull
    private Integer year;

    @NotNull
    @Min(0)
    private Double primaryTargetSet;

    @NotNull
    @Min(0)
    private Double secondaryTargetSet;

}

