package com.instantsolutions.larimarpharma.DTOs;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class FieldExecutiveUpdate {


    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid Indian phone number"
    )
    private String phone;


    @NotBlank(message = "Region is required")
    private String region;

    @NotEmpty(message="market names are requires")
    @Size(min = 1)
    private List<String> markets;

    @NotNull(message = "Manager id is required")
    private Long managerId;

    @NotBlank(message = "Territory is required")
    private String territory;
}
