package com.instantsolutions.larimarpharma.DTOs;
import lombok.Data;
@Data
public class AdminReviewRequest {
    private String action; // "APPROVE" or "REJECT"
    private String notes;
}