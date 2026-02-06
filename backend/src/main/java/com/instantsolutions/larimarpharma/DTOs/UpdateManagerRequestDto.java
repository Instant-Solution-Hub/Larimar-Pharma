package com.instantsolutions.larimarpharma.DTOs;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateManagerRequestDto {


    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String name;


    @NotBlank(message = "is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "must be a valid 10 digit phone number")
    private String phone;

    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String employeeCode;


    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String department;


    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String designation;
}
