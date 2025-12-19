package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class FEUpdateContactDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must be exactly 10 digits"
    )
    private String phone;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Emergency contact must be exactly 10 digits"
    )
    private String emergencyContact;

}

