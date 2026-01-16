package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.entity.Stockist;
import com.instantsolutions.larimarpharma.entity.Visit;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MarkStockistVisitRequestDto {
    private Long fieldExecutiveId;
    private Long stockistId;
    private Visit.StockistType stockistType;
    @NotNull
    private Integer weekNumber;   // 1–5

    @NotNull
    private Integer dayOfWeek;    // 1 (Mon) – 7 (Sun)

    private Visit.VisitStatus status;
    private String notes;
    private List<String> activitiesPerformed;
    private Double orderValue;
    private String location;
}
