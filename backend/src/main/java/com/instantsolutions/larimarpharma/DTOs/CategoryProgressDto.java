package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Data
@Builder
public class CategoryProgressDto {

    private String category;       // A_PLUS, A, B
    private String label;          // A+, A, B

    private int targetDoctors;
    private int visitsPerDoctor;

    private int completedDoctors;  // DISTINCT doctors visited
    private int completedVisits;   // TOTAL visits completed
}

