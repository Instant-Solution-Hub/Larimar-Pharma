package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.LeaveRequest;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminLeaveRequestDto {

    private Long adminId;
    private LeaveRequest.LeaveType leaveType;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String reason;
}

