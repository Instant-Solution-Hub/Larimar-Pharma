package com.instantsolutions.larimarpharma.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerRequestDto {

    // ===== BaseUser fields =====

    @Schema(example = "string")
    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String name;

    @Schema(example = "string")
    @NotBlank(message = "is required")
    @Email(message = "must be a valid email address")
    private String email;

    @Schema(example = "string")
    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String password;

    @Schema(example = "string")
    @NotBlank(message = "is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "must be a valid 10 digit phone number")
    private String phone;

    private boolean active;

    // ===== Manager-specific fields =====

    @Schema(example = "string")
    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String employeeCode;

    @Schema(example = "string")
    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String department;

    @Schema(example = "string")
    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String designation;

    // ===== Simple collections =====
    private Set<String> managedTerritories;

    // ===== Trim setters =====
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode == null ? null : employeeCode.trim();
    }

    public void setDepartment(String department) {
        this.department = department == null ? null : department.trim();
    }

    public void setDesignation(String designation) {
        this.designation = designation == null ? null : designation.trim();
    }
}
