package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Doctor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DoctorRequestDto {
    @Schema(example = "string")
    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String name;
    private Doctor.Category category;
    private Doctor.PracticeType practiceType;
    @Schema(example = "string")
    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String designation;
    @Schema(example = "string")
    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String hospitalName;
    @Schema(example = "string")
    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String location;
    @Schema(example = "string")
    @NotBlank(message = "is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "must be a valid 10 digit phone number")
    private String contactNumber;
    @Schema(example = "string")
    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String doctorCode;
    private boolean active;

}
