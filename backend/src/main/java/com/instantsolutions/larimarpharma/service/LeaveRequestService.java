package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.LeaveRequestDto;
import com.instantsolutions.larimarpharma.DTOs.LeaveRequestWithFEResponseDto;
import com.instantsolutions.larimarpharma.DTOs.ManagerLeaveResponseDto;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.exceptions.InsufficientLeaveBalanceException;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.LeaveRequestRepository;
import com.instantsolutions.larimarpharma.repository.ManagerVisitRepository;
import com.instantsolutions.larimarpharma.repository.VisitRepository;
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
    @Autowired
    private VisitRepository visitRepository;
    @Autowired
    private ManagerVisitRepository managerVisitRepository;

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
                .appliedDate(LocalDateTime.now())
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

    private boolean hasSufficientBalance(ManagerProfile profile,
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
    public LeaveRequestWithFEResponseDto approveLeave(Long leaveId) {

        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new EntityNotFoundException("Leave not found"));

        if (leave.getStatus() != LeaveRequest.ApprovalStatus.PENDING) {
            throw new IllegalStateException("Leave already processed");
        }

        FieldExecutiveProfile profile = leave.getFieldExecutive().getProfile();
        int days = calculateDays(leave.getFromDate(), leave.getToDate());
        if (!hasSufficientBalance(profile, leave.getLeaveType(), days)) {
           return rejectLeave(leaveId);

        }
            deductLeaveBalance(profile, leave.getLeaveType(), days);

            leave.setStatus(LeaveRequest.ApprovalStatus.APPROVED);
            leave.setApprovalDate(LocalDateTime.now());
        // Only apply for FieldExecutive leave
        if (leave.getFieldExecutive() != null) {

            Long fieldExecutiveId = leave.getFieldExecutive().getId();

            visitRepository.markVisitsAsMissedForLeave(
                    fieldExecutiveId,
                    leave.getFromDate(),
                    leave.getToDate(),
                    "Marked as MISSED due to approved leave from "
                            + leave.getFromDate().toLocalDate()
                            + " to "
                            + leave.getToDate().toLocalDate()
            );
        }

            leaveRequestRepository.save(leave);
            return LeaveRequestWithFEResponseDto.builder()
                    .id(leave.getId())
                    .feCode(leave.getFieldExecutive().getEmployeeCode())
                    .feName(leave.getFieldExecutive().getName())
                    .leaveType(leave.getLeaveType())
                    .status(leave.getStatus())
                    .fromDate(leave.getFromDate())
                    .toDate(leave.getToDate())
                    .reason(leave.getReason())
                    .appliedDate(leave.getAppliedDate())
                    .build();

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
    private void deductLeaveBalance(ManagerProfile profile,
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

    @Transactional

    public LeaveRequestWithFEResponseDto rejectLeave(Long leaveId) {

        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new EntityNotFoundException("Leave not found"));

        leave.setStatus(LeaveRequest.ApprovalStatus.REJECTED);
        leave.setApprovalDate(LocalDateTime.now());

         leaveRequestRepository.save(leave);
        return  LeaveRequestWithFEResponseDto.builder()
                .id(leave.getId())
                .feCode(leave.getFieldExecutive().getEmployeeCode())
                .feName(leave.getFieldExecutive().getName())
                .leaveType(leave.getLeaveType())
                .status(leave.getStatus())
                .fromDate(leave.getFromDate())
                .toDate(leave.getToDate())
                .reason(leave.getReason())
                .appliedDate(leave.getAppliedDate())
                .build();
    }


    public List<LeaveRequest> getLeavesByFieldExecutive(Long feId) {
        return leaveRequestRepository.findByFieldExecutiveIdOrderByFromDateDesc(feId);
    }


    @Transactional
    public List<LeaveRequestWithFEResponseDto> getLeavesOfFEsUnderManager(Long managerId) {
        List<LeaveRequest> leaves =  leaveRequestRepository
                .findByFieldExecutive_Manager_IdOrderByFromDateDesc(managerId);
        return leaves.stream()
                .map(lr -> LeaveRequestWithFEResponseDto.builder()
                        .id(lr.getId())
                        .feCode(lr.getFieldExecutive().getEmployeeCode())
                        .feName(lr.getFieldExecutive().getName())
                        .leaveType(lr.getLeaveType())
                        .status(lr.getStatus())
                        .fromDate(lr.getFromDate())
                        .toDate(lr.getToDate())
                        .reason(lr.getReason())
                        .appliedDate(lr.getAppliedDate())
                        .build()
                )
                .toList();
    }

    @Transactional
    public List<ManagerLeaveResponseDto> getAllManagerLeaves() {

        List<LeaveRequest> leaves = leaveRequestRepository.findAllManagerLeaves();

        return leaves.stream().map(leave ->
                ManagerLeaveResponseDto.builder()
                        .id(leave.getId())
                        .managerCode(leave.getManager().getEmployeeCode())
                        .managerName(leave.getManager().getName())
                        .leaveType(leave.getLeaveType())
                        .fromDate(leave.getFromDate())
                        .toDate(leave.getToDate())
                        .reason(leave.getReason())
                        .status(leave.getStatus())
                        .appliedDate(leave.getAppliedDate())
                        .build()
        ).toList();
    }





    private int calculateDays(LocalDateTime from, LocalDateTime to) {
        return (int) (to.toLocalDate().toEpochDay()
                - from.toLocalDate().toEpochDay()) + 1;
    }

    @Transactional
    public ManagerLeaveResponseDto approveManagerLeave(Long leaveId) {

        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new EntityNotFoundException("Leave not found"));

        if (leave.getStatus() != LeaveRequest.ApprovalStatus.PENDING) {
            throw new IllegalStateException("Leave already processed");
        }

        ManagerProfile profile = leave.getManager().getProfile();
        Manager manager = leave.getManager();
        int days = calculateDays(leave.getFromDate(), leave.getToDate());
        if (!hasSufficientBalance(profile, leave.getLeaveType(), days)) {
            return rejectManagerLeave(leaveId);

        }
        deductLeaveBalance(profile, leave.getLeaveType(), days);

        leave.setStatus(LeaveRequest.ApprovalStatus.APPROVED);
        leave.setApprovalDate(LocalDateTime.now());

        // AUTO-MARK MANAGER VISITS AS MISSED
        String autoNote = "Marked as MISSED due to approved leave from "
                + leave.getFromDate().toLocalDate()
                + " to "
                + leave.getToDate().toLocalDate();

        managerVisitRepository.markManagerVisitsAsMissedForLeave(
                manager.getId(),
                leave.getFromDate(),
                leave.getToDate(),
                autoNote
        );


        leaveRequestRepository.save(leave);
        return ManagerLeaveResponseDto.builder()
                .id(leave.getId())
                .managerCode(leave.getManager().getEmployeeCode())
                .managerName(leave.getManager().getName())
                .leaveType(leave.getLeaveType())
                .status(leave.getStatus())
                .fromDate(leave.getFromDate())
                .toDate(leave.getToDate())
                .reason(leave.getReason())
                .appliedDate(leave.getAppliedDate())
                .build();
    }


    @Transactional
    public ManagerLeaveResponseDto rejectManagerLeave(Long leaveId) {

        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new EntityNotFoundException("Leave not found"));

        leave.setStatus(LeaveRequest.ApprovalStatus.REJECTED);
        leave.setApprovalDate(LocalDateTime.now());

        leaveRequestRepository.save(leave);
        return  ManagerLeaveResponseDto.builder()
                .id(leave.getId())
                .managerCode(leave.getManager().getEmployeeCode())
                .managerName(leave.getManager().getName())
                .leaveType(leave.getLeaveType())
                .status(leave.getStatus())
                .fromDate(leave.getFromDate())
                .toDate(leave.getToDate())
                .reason(leave.getReason())
                .appliedDate(leave.getAppliedDate())
                .build();
    }


    @Transactional
    public List<LeaveRequestWithFEResponseDto> getLeavesOfFEs() {
        List<LeaveRequest> leaves =  leaveRequestRepository.findAllFELeaves();
        return leaves.stream()
                .map(lr -> LeaveRequestWithFEResponseDto.builder()
                        .id(lr.getId()).feCode(
                                lr.getFieldExecutive() != null
                                        ? lr.getFieldExecutive().getEmployeeCode()
                                        : null
                        )
                        .feName(
                                lr.getFieldExecutive() != null
                                        ? lr.getFieldExecutive().getName()
                                        : null
                        )
                        .leaveType(lr.getLeaveType())
                        .status(lr.getStatus())
                        .fromDate(lr.getFromDate())
                        .toDate(lr.getToDate())
                        .reason(lr.getReason())
                        .appliedDate(lr.getAppliedDate())
                        .build()
                )
                .toList();
    }
}
