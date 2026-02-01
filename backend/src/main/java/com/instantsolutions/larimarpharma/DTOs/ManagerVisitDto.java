package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerVisitDto {
    private String name;
    private String time;
    private String type; // "doctor", "pharmacy", "stockist"
    private String feName;
    private LocalDateTime scheduledDate;
}
