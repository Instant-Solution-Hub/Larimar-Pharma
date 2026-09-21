package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.ManagerFieldExecutiveRequest;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerFeRequestResponseDto {

    private Long requestId;
    private Long managerId;
    private String managerName;

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

    public static ManagerFeRequestResponseDto fromEntity(ManagerFieldExecutiveRequest r) {
        return ManagerFeRequestResponseDto.builder()
                .requestId(r.getId())
                .managerId(r.getManager().getId())
                .managerName(r.getManager().getName())
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