package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestNewFieldExecutiveDto {

    @NotNull
    private Long zsmId;

    @NotNull
    private Long requestedFieldExecutiveId;

    // Optional - the FE being replaced (if known)
    private Long currentFieldExecutiveId;

    @NotNull
    private Integer weekNumber;

    @NotNull
    private Integer dayOfWeek;

    @NotBlank
    private String reason;
}