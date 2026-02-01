package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.DashboardStatsDto;
import com.instantsolutions.larimarpharma.DTOs.FieldExecutiveResponse;
import com.instantsolutions.larimarpharma.DTOs.ManagerRequestDto;
import com.instantsolutions.larimarpharma.DTOs.ManagerResponseDto;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.entity.ManagerProfile;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.ManagerRepository;
import com.instantsolutions.larimarpharma.repository.ManagerVisitRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final FEService feService;
//    private final ManagerProfileRepository managerProfileRepository;
    private final ManagerVisitRepository managerVisitRepository;

    public Manager createManager(ManagerRequestDto dto) {
        Manager manager = Manager.builder()
                .name(dto.getName().toUpperCase())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .phone(dto.getPhone())
                .active(dto.isActive())
                .employeeCode(dto.getEmployeeCode().toUpperCase())
                .department(dto.getDepartment())
                .designation(dto.getDesignation())
                .managedTerritories(dto.getManagedTerritories())
                .build();

        return managerRepository.save(manager);
    }

    public Manager updateManager(Long id, ManagerRequestDto dto) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Manager not found with id: " + id));

        manager.setName(dto.getName().toUpperCase());
        manager.setEmail(dto.getEmail());
        manager.setPassword(dto.getPassword());
        manager.setPhone(dto.getPhone());
        manager.setActive(dto.isActive());
        manager.setEmployeeCode(dto.getEmployeeCode().toUpperCase());
        manager.setDepartment(dto.getDepartment());
        manager.setDesignation(dto.getDesignation().toUpperCase());
        manager.setManagedTerritories(dto.getManagedTerritories());

        return managerRepository.save(manager);
    }


    public ManagerResponseDto getManagerById(Long id) {
        Manager manager = managerRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Manager not found with id: " + id));
        return toManagerResponseDto(manager);
    }

    public List<FieldExecutiveResponse> getFieldExecutivesByManagerId(Long id) {
        List<FieldExecutive> fes = fieldExecutiveRepository.findByManagerId(id);
        return fes.stream()
                .map(feService::mapToResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<ManagerResponseDto> getAllManagers() {
        return managerRepository.findAllWithExecutives()
                .stream()
                .map(this::toManagerResponseDto)
                .toList();
    }


    public void deleteManager(Long id) {
        if (!managerRepository.existsById(id)) {
            throw new EntityNotFoundException("Manager not found with id: " + id);
        }
        managerRepository.deleteById(id);
    }

    public ManagerResponseDto toManagerResponseDto(Manager m) {
        return ManagerResponseDto.builder()
                .id(m.getId())
                .name(m.getName())
                .email(m.getEmail())
                .phone(m.getPhone())
                .employeeCode(m.getEmployeeCode())
                .department(m.getDepartment())
                .designation(m.getDesignation())
                .managedTerritories(m.getManagedTerritories())
                .fieldExecutives(
                        m.getFieldExecutives()
                                .stream()
                                .map(fe -> FieldExecutiveResponse.builder()
                                        .id(fe.getId())
                                        .name(fe.getName())
                                        .email(fe.getEmail())
                                        .phone(fe.getPhone())
                                        .employeeCode(fe.getEmployeeCode())
                                        .build()
                                ).toList()
                )
                .build();
    }

    /**
     * Get dashboard stats for a manager
     */
//    public DashboardStatsDto getDashboardStats(Long managerId) {
//        Manager manager = managerRepository.findById(managerId)
//                .orElseThrow(() -> new RuntimeException("Manager not found"));
//
//        // Get manager profile
//        ManagerProfile profile = managerProfileRepository.findByManagerId(managerId)
//                .orElseGet(() -> ManagerProfile.builder()
//                        .teamSize(0)
//                        .teamTargetAchieved(0.0)
//                        .teamComplianceRate(0.0)
//                        .build());
//
//        LocalDate today = LocalDate.now();
//
//        // First day of current month at 00:00
//        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
//
//        // Last day of current month at 23:59:59.999999999
//        LocalDateTime endOfMonth = today
//                .withDayOfMonth(today.lengthOfMonth())
//                .atTime(LocalTime.MAX);
//
//        int totalVisitsThisMonth = managerVisitRepository
//                .countByManagerIdAndScheduledDateBetween(
//                        managerId,
//                        startOfMonth,
//                        endOfMonth
//                );
//        int teamSize = fieldExecutiveRepository.countByManagerId(managerId);
//
//
//        // Calculate trend (simplified - could be enhanced with historical data)
//        double teamTargetProgress = profile.getTeamTargetAchieved() != null ?
//                profile.getTeamTargetAchieved() : 0.0;
//
//        String trend = calculateTrend(managerId);
//
//        return DashboardStatsDto.builder()
//                .totalVisits(totalVisitsThisMonth)
//                .teamTargetProgress(teamTargetProgress)
//                .totalMembers(teamSize)
//                .trend(trend)
//                .build();
//    }

    private String calculateTrend(Long managerId) {
        // Simplified trend calculation
        // In real implementation, you might compare with previous week/month
        LocalDate today = LocalDate.now();
        LocalDate lastWeek = today.minusWeeks(1);

        // Count visits this week vs last week
        LocalDateTime startOfThisWeek = today.minusDays(today.getDayOfWeek().getValue() - 1).atStartOfDay();
        LocalDateTime endOfThisWeek = startOfThisWeek.plusDays(6).with(LocalTime.MAX);

        LocalDateTime startOfLastWeek = startOfThisWeek.minusWeeks(1);
        LocalDateTime endOfLastWeek = endOfThisWeek.minusWeeks(1);

        int visitsThisWeek = managerVisitRepository.countByManagerIdAndScheduledDateBetween(
                managerId, startOfThisWeek, endOfThisWeek);
        int visitsLastWeek = managerVisitRepository.countByManagerIdAndScheduledDateBetween(
                managerId, startOfLastWeek, endOfLastWeek);

        if (visitsLastWeek == 0) return "+0%";

        double percentageChange = ((double) (visitsThisWeek - visitsLastWeek) / visitsLastWeek) * 100;
        String sign = percentageChange >= 0 ? "+" : "";

        return String.format("%s%d%% from last week", sign, Math.round(percentageChange));
    }


}
