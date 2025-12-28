package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.LeaveRequest.LeaveType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LeaveRequestDto {

    @NotNull(message = "Field Executive ID is required")
    @Positive(message = "Field Executive ID must be a positive number")
    private Long fieldExecutiveId;

    @NotNull(message = "Leave type is required")
    private LeaveType leaveType;

    @NotNull(message = "From date is required")
    @FutureOrPresent(message = "From date cannot be in the past")
    private LocalDateTime fromDate;

    @NotNull(message = "To date is required")
    @FutureOrPresent(message = "To date cannot be in the past")
    private LocalDateTime toDate;

    private String reason;
}
