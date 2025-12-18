package com.instantsolutions.larimarpharma.DTOs;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "userType"
)

public class LoginResponseDto {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private String userType;
    private String token;
    private String refreshToken;
    private boolean active;

    // Common fields for all users
    private String profileImageUrl;
    private String emergencyContact;

    // Timestamps
    private String createdAt;
    private String lastLogin;

    // Common response constructor
    public LoginResponseDto(
            Long id,
            String email,
            String name,
            String phone,
            String userType,
            boolean active,
            String emergencyContact,
            String createdAt
    ) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.userType = userType;
        this.active = active;
        this.emergencyContact = emergencyContact;
        this.createdAt = createdAt;
    }
}