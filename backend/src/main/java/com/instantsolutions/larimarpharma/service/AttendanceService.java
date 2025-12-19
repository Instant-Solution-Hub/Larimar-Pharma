package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.entity.FieldExecutiveProfile;
import com.instantsolutions.larimarpharma.entity.LeaveRequest;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveProfileRepository;
import com.instantsolutions.larimarpharma.repository.LeaveRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    @Autowired
    LeaveRequestRepository leaveRequestRepository;

    @Autowired
    FieldExecutiveProfileRepository profileRepository;

    @Transactional
    public Integer calculateMonthlyAttendance(
            Long feId, int year, int month) {

        // Month boundaries
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        // Total working days (Mon–Sat)
        long totalWorkingDays = start.datesUntil(end.plusDays(1))
                .filter(d -> !(d.getDayOfWeek() == DayOfWeek.SUNDAY))
                .count();

        // Approved leaves
        List<LeaveRequest> approvedLeaves =
                leaveRequestRepository.findApprovedLeavesForMonth(
                        feId, startDateTime, endDateTime);

        long approvedLeaveDays = approvedLeaves.stream()
                .mapToLong(this::calculateLeaveDays)
                .sum();

        long presentDays = totalWorkingDays - approvedLeaveDays;
        presentDays = Math.max(presentDays, 0);

        int attendancePercentage =
                (int) ((presentDays * 100) / totalWorkingDays);

        // Persist in profile
        FieldExecutiveProfile profile =
                profileRepository.findByFieldExecutiveId(feId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Field Executive profile not found"));

        profile.setAttendancePercentage(attendancePercentage);

        return attendancePercentage;
    }

    private long calculateLeaveDays(LeaveRequest leave) {
        LocalDate start = leave.getFromDate().toLocalDate();
        LocalDate end = leave.getToDate().toLocalDate();

        return end.toEpochDay() - start.toEpochDay() + 1;
    }
}
