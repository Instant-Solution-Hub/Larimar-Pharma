package com.instantsolutions.larimarpharma.DTOs;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserIdentityDto {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "User type is required")
    private String userType; // "FIELD_EXECUTIVE" or "MANAGER"
}
