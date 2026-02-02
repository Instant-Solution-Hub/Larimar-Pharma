package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerJoiningResponse2Dto {


    private Long id;
    private Long feId;
    private String feName;
    private String doctorName;
    private String hospital;
    private LocalDateTime scheduledTime;
    private LocalDateTime joiningTime;
    private String status;
    private String notes;
    private LocalDateTime date;
}
