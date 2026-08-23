package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignZsmVisitRequest {
    @NotNull
    private Long zsmId;

    @NotNull
    private Long fieldExecutiveId;

    @NotNull
    private Integer weekNumber;

    @NotNull
    private Integer dayOfWeek;
}
