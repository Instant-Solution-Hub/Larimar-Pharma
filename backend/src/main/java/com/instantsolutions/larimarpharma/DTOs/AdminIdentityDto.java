package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminIdentityDto {
    @NotNull(message = "Admin ID is required")
    private Long adminId;
}

