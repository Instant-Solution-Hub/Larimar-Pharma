package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.ZsmFieldExecutiveRequest;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZsmFeRequestResponseDto {

    private Long requestId;
    private Long zsmId;
    private String zsmName;

    private Long requestedFeId;
    private String requestedFeName;
    private String requestedFeEmpCode;

    private Long currentFeId;
    private String currentFeName;

    private Integer weekNumber;
    private Integer dayOfWeek;
    private LocalDate targetDate;

    private String reason;
    private String status;
    private String adminRemarks;

    private String reviewedByName;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;

    public static ZsmFeRequestResponseDto fromEntity(ZsmFieldExecutiveRequest r) {
        return ZsmFeRequestResponseDto.builder()
                .requestId(r.getId())
                .zsmId(r.getZsmAdmin().getId())
                .zsmName(r.getZsmAdmin().getName())
                .requestedFeId(r.getRequestedFieldExecutive().getId())
                .requestedFeName(r.getRequestedFieldExecutive().getName())
                .requestedFeEmpCode(r.getRequestedFieldExecutive().getEmployeeCode())
                .currentFeId(r.getCurrentFieldExecutive() != null
                        ? r.getCurrentFieldExecutive().getId() : null)
                .currentFeName(r.getCurrentFieldExecutive() != null
                        ? r.getCurrentFieldExecutive().getName() : null)
                .weekNumber(r.getWeekNumber())
                .dayOfWeek(r.getDayOfWeek())
                .targetDate(r.getTargetDate())
                .reason(r.getReason())
                .status(r.getStatus().name())
                .adminRemarks(r.getAdminRemarks())
                .reviewedByName(r.getReviewedBy() != null
                        ? r.getReviewedBy().getName() : null)
                .reviewedAt(r.getReviewedAt())
                .createdAt(r.getCreatedAt())
                .build();
    }
}