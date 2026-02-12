package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Visit;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
public class CreateUnscheduledManagerVisitRequest {

    @NotNull
    private Long managerId;

//    @NotNull
//    private Long fieldExecutiveId;

    @NotNull
    private Long doctorId;

//    @NotNull
//    private Visit.VisitType visitType;

    private String notes;
    private List<String> activitiesPerformed;
}

