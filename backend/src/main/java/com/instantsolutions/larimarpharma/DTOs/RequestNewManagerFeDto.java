package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestNewManagerFeDto {

    @NotNull
    private Long managerId;

    @NotNull
    private Long requestedFieldExecutiveId;

    private Long currentFieldExecutiveId;

    @NotNull
    private Integer weekNumber;

    @NotNull
    private Integer dayOfWeek;

    @NotBlank
    private String reason;
}