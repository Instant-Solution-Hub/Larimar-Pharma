package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ManualLockRequestDto extends AdminIdentityDto {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "User type is required")
    private String userType;

    @NotNull(message = "Lock date is required")
    private LocalDate lockDate;

    @NotNull(message = "Reason is required")
    private String reason;
}
