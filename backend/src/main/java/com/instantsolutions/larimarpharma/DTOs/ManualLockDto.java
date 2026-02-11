package com.instantsolutions.larimarpharma.DTOs;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ManualLockDto {
    private String userType; // "FIELD_EXECUTIVE" or "MANAGER"
    private Long userId;
    private LocalDate lockDate;
    private String reason;
}