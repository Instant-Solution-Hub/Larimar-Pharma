package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerJoiningResponseDto {

    private Long id;
    private Long fieldExecutiveId;
    private Long managerId;
    private Long doctorId;
    private LocalDateTime scheduledTime;
    private LocalDateTime actualJoiningTime;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
}
