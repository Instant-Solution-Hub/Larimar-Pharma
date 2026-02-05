package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.AdminLeaveRequestDto;
import com.instantsolutions.larimarpharma.entity.Admin;
import com.instantsolutions.larimarpharma.entity.AdminProfile;
import com.instantsolutions.larimarpharma.entity.LeaveRequest;
import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.exceptions.InsufficientLeaveBalanceException;
import com.instantsolutions.larimarpharma.repository.AdminRepository;
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
public class AdminLeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private AdminRepository adminRepository;

    public LeaveRequest applyLeave(AdminLeaveRequestDto dto) {

        Admin admin = adminRepository.findById(dto.getAdminId())
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));

        AdminProfile profile = admin.getProfile();
        if (profile == null) {
            throw new IllegalStateException("Admin profile not found");
        }

        int daysRequested = calculateDays(dto.getFromDate(), dto.getToDate());

        if (!hasSufficientBalance(profile, dto.getLeaveType(), daysRequested)) {
            throw new InsufficientLeaveBalanceException(
                    "Not enough " + dto.getLeaveType() + " balance"
            );
        }

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .admin(admin)
                .leaveType(dto.getLeaveType())
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .reason(dto.getReason())
                .status(LeaveRequest.ApprovalStatus.PENDING)
                .build();


        return leaveRequestRepository.save(leaveRequest);
    }

//    @Transactional
//    public LeaveRequest approveLeave(Long leaveId, Long adminId) {
//
//        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
//                .orElseThrow(() -> new EntityNotFoundException("Leave not found"));
//
//        if (leave.getStatus() != LeaveRequest.ApprovalStatus.PENDING) {
//            throw new IllegalStateException("Leave already processed");
//        }
//
//        ManagerProfile profile = leave.getManager().getProfile();
//        int days = calculateDays(leave.getFromDate(), leave.getToDate());
//
//        deductLeaveBalance(profile, leave.getLeaveType(), days);
//
//        leave.setStatus(LeaveRequest.ApprovalStatus.APPROVED);
//        leave.setApprovalDate(LocalDateTime.now());
//
//        return leave;
//    }

    public List<LeaveRequest> getLeavesByAdmin(Long adminId) {

        Admin manager = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));

        return leaveRequestRepository
                .findByAdminIdOrderByFromDateDesc(adminId);
    }
    @Transactional
    public int getConfirmedLeavesForCurrentMonth(Long adminId) {

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));

        LocalDate today = LocalDate.now();

        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = today
                .withDayOfMonth(today.lengthOfMonth())
                .atTime(23, 59, 59);

        List<LeaveRequest> leaves =
                leaveRequestRepository.findApprovedAdminLeavesForMonth(
                        adminId,
                        monthStart,
                        monthEnd
                );

        int totalDays = 0;

        for (LeaveRequest leave : leaves) {

            LocalDateTime effectiveStart =
                    leave.getFromDate().isBefore(monthStart)
                            ? monthStart
                            : leave.getFromDate();

            LocalDateTime effectiveEnd =
                    leave.getToDate().isAfter(monthEnd)
                            ? monthEnd
                            : leave.getToDate();

            long days = Duration.between(
                    effectiveStart.toLocalDate().atStartOfDay(),
                    effectiveEnd.toLocalDate().plusDays(1).atStartOfDay()
            ).toDays();

            totalDays += days;
        }

        return totalDays;
    }

    /* -------------------- Helpers -------------------- */

    private void deductLeaveBalance(
            AdminProfile profile,
            LeaveRequest.LeaveType type,
            int days
    ) {
        switch (type) {
            case CASUAL_LEAVE ->
                    profile.setApprovedCasualLeaves(
                            profile.getApprovedCasualLeaves() + days
                    );
            case SICK_LEAVE ->
                    profile.setApprovedSickLeaves(
                            profile.getApprovedSickLeaves() + days
                    );
            case EARNED_LEAVE -> {
            }
        }
    }

    private int calculateDays(LocalDateTime from, LocalDateTime to) {
        return (int) (
                to.toLocalDate().toEpochDay()
                        - from.toLocalDate().toEpochDay()
        ) + 1;
    }

    private boolean hasSufficientBalance(
            AdminProfile profile,
            LeaveRequest.LeaveType type,
            int days
    ) {
        return switch (type) {
            case CASUAL_LEAVE ->
                    profile.getCasualLeaves()
                            - profile.getApprovedCasualLeaves() >= days;
            case SICK_LEAVE ->
                    profile.getSickLeaves()
                            - profile.getApprovedSickLeaves() >= days;
            case EARNED_LEAVE -> true;
        };
    }
}

