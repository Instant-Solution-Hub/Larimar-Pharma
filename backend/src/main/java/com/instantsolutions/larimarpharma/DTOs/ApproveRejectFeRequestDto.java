package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApproveRejectFeRequestDto {

    @NotNull
    private Long requestId;

    @NotNull
    private Long adminId;

    // Optional remarks when approving/rejecting
    private String adminRemarks;
}