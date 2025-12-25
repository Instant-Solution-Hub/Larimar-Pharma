package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Visit;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VisitResponseDto {

    private Long id;
    private LocalDate visitDate;

    private Long fieldExecutiveId;
    private String fieldExecutiveName;

    private Long doctorId;
    private Long pharmacyId;

    private Visit.StockistType stockistType;
}
