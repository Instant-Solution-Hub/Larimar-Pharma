package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Visit;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VisitPlanByWeekDto {

    @NotNull
    private Long fieldExecutiveId;

    private Long doctorId;

    private Long pharmacistId;

    @NotNull
    private Integer weekNumber;   // 1–5

    @NotNull
    private Integer dayOfWeek;    // 1 (Mon) – 7 (Sun)

    @NotNull
    private Visit.VisitType visitType;

    // Optional
    private String pharmacyName;
    private String contactPerson;
    private String contactNumber;
    private Visit.StockistType stockistType;
    private String stockistName;
}
