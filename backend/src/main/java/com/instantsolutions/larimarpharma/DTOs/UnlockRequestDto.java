package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UnlockRequestDto  {
    @NotNull(message = "User ID is required")
    private Long userId;
    @NotNull(message = "User type is required")
    private String userType; // "FIELD_EXECUTIVE" or "MANAGER"
    @NotNull(message = "Reason is required")
    private String reason;
}
