package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerJoiningResponse4Dto {

    private Long id;

    private String managerId;
    private String managerName;

    private String feId;
    private String feName;

    private String doctorName;
    private String hospital;

    private String date;          // formatted date
    private String scheduledTime; // HH:mm
    private String joiningTime;   // HH:mm

    private String notes;
    private String status;
}