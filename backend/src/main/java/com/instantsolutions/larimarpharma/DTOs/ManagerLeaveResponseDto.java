package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.LeaveRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManagerLeaveResponseDto {

    private Long id;
    private String managerCode;
    private String managerName;
    private LeaveRequest.LeaveType leaveType;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String reason;
    private LeaveRequest.ApprovalStatus status;
    private LocalDateTime appliedDate;

}
