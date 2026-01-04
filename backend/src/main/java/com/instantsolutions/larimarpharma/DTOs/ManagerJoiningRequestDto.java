package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerJoiningRequestDto {

    @NotNull(message = "Field Executive ID is required")
    private Long fieldExecutiveId;

    @NotNull(message = "Manager ID is required")
    private Long managerId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Scheduled time is required")
    @FutureOrPresent(message = "Scheduled time cannot be in the past")
    private LocalDateTime scheduledTime;

    private LocalDateTime actualJoiningTime;

    private ManagerJoiningStatus status;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    public enum ManagerJoiningStatus {
        ON_TIME, EARLY, LATE
    }
}
