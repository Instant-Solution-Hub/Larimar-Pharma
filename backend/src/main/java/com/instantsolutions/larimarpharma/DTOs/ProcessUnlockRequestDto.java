package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProcessUnlockRequestDto extends AdminIdentityDto {
    @NotNull(message = "Approve flag is required")
    private boolean approve;

    private String comments;
}
