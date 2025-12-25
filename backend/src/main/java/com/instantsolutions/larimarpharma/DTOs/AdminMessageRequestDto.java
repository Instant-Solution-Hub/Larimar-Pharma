package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminMessageRequestDto {

    @NotBlank
    private String subject;

    @NotBlank
    private String message;
}
