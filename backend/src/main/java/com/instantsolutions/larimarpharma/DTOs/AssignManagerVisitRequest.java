package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignManagerVisitRequest {

    @NotNull
    private Long managerId;

    @NotNull
    private Long fieldExecutiveId;

    @NotNull
    private Integer weekNumber;

    @NotNull
    private Integer dayOfWeek;
}

