package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitResponse {
    private Long id;
    private String doctorName;
    private String specialization;
    private String location;
    private String time;
    private String status; // "completed", "pending", "missed"
}