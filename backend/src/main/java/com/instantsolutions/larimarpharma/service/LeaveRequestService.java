package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.LeaveRequestDto;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.FieldExecutiveProfile;
import com.instantsolutions.larimarpharma.entity.LeaveRequest;
import com.instantsolutions.larimarpharma.exceptions.InsufficientLeaveBalanceException;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.LeaveRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private FieldExecutiveRepository fieldExecutiveRepository;

    public LeaveRequest applyLeave(LeaveRequestDto dto) {

        FieldExecutive fe = fieldExecutiveRepository
                .findById(dto.getFieldExecutiveId())
                .orElseThrow(() -> new EntityNotFoundException("Field Executive not found"));

        FieldExecutiveProfile profile = fe.getProfile();
        if (profile == null) {
            throw new IllegalStateException("Field Executive profile not found");
        }

        int daysRequested = calculateDays(dto.getFromDate(), dto.getToDate());

        if (!hasSufficientBalance(profile, dto.getLeaveType(), daysRequested)) {
            throw new InsufficientLeaveBalanceException(
                    "Not enough " + dto.getLeaveType() + " balance"
            );
        }


        LeaveRequest leaveRequest = LeaveRequest.builder()
                .fieldExecutive(fe)
                .leaveType(dto.getLeaveType())
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .reason(dto.getReason())
                .status(LeaveRequest.ApprovalStatus.PENDING)
                .build();

        return leaveRequestRepository.save(leaveRequest);
    }

    @Transactional
    public int getConfirmedLeavesForCurrentMonth(Long feId) {

        LocalDate today = LocalDate.now();

        LocalDateTime monthStart = today
                .withDayOfMonth(1)
                .atStartOfDay();

        LocalDateTime monthEnd = today
                .withDayOfMonth(today.lengthOfMonth())
                .atTime(23, 59, 59); // important: end of day

        List<LeaveRequest> leaves = leaveRequestRepository
                .findApprovedLeavesForMonth(
                        feId,
                        monthStart,
                        monthEnd
                );

        int totalDays = 0;

        for (LeaveRequest leave : leaves) {

            LocalDateTime leaveStart = leave.getFromDate();
            LocalDateTime leaveEnd = leave.getToDate();

            // Clamp leave range to current month
            LocalDateTime effectiveStart =
                    leaveStart.isBefore(monthStart) ? monthStart : leaveStart;

            LocalDateTime effectiveEnd =
                    leaveEnd.isAfter(monthEnd) ? monthEnd : leaveEnd;

            long days = Duration.between(
                    effectiveStart.toLocalDate().atStartOfDay(),
                    effectiveEnd.toLocalDate().plusDays(1).atStartOfDay()
            ).toDays();

            totalDays += days;
        }

        return totalDays;
    }


    private boolean hasSufficientBalance(FieldExecutiveProfile profile,
                                         LeaveRequest.LeaveType type,
                                         int days) {

        return switch (type) {
            case CASUAL_LEAVE -> profile.getCasualLeaves() != null
                    && profile.getCasualLeaves() - profile.getApprovedCasualLeaves() >= days;

            case SICK_LEAVE -> profile.getSickLeaves() != null
                    && profile.getSickLeaves() - profile.getApprovedSickLeaves() >= days;

            case EARNED_LEAVE -> true; // optional rule
        };
    }

    @Transactional
    public LeaveRequest approveLeave(Long leaveId, Long managerId) {

        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new EntityNotFoundException("Leave not found"));

        if (leave.getStatus() != LeaveRequest.ApprovalStatus.PENDING) {
            throw new IllegalStateException("Leave already processed");
        }

        FieldExecutiveProfile profile = leave.getFieldExecutive().getProfile();
        int days = calculateDays(leave.getFromDate(), leave.getToDate());

        deductLeaveBalance(profile, leave.getLeaveType(), days);

        leave.setStatus(LeaveRequest.ApprovalStatus.APPROVED);
        leave.setApprovalDate(LocalDateTime.now());

        return leave;
    }

    private void deductLeaveBalance(FieldExecutiveProfile profile,
                                    LeaveRequest.LeaveType type,
                                    int days) {

        switch (type) {
            case CASUAL_LEAVE ->
                    profile.setApprovedCasualLeaves(profile.getApprovedCasualLeaves() + days);

            case SICK_LEAVE ->
                    profile.setApprovedSickLeaves(profile.getApprovedSickLeaves() + days);

            case EARNED_LEAVE -> {
                // optional logic
            }
        }

    }

    public LeaveRequest rejectLeave(Long leaveId, Long managerId) {

        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new EntityNotFoundException("Leave not found"));

        leave.setStatus(LeaveRequest.ApprovalStatus.REJECTED);
        leave.setApprovalDate(LocalDateTime.now());

        return leaveRequestRepository.save(leave);
    }


    public List<LeaveRequest> getLeavesByFieldExecutive(Long feId) {
        return leaveRequestRepository.findByFieldExecutiveIdOrderByFromDateDesc(feId);
    }



    private int calculateDays(LocalDateTime from, LocalDateTime to) {
        return (int) (to.toLocalDate().toEpochDay()
                - from.toLocalDate().toEpochDay()) + 1;
    }
}
