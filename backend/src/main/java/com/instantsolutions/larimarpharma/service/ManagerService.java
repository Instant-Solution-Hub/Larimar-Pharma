package com.instantsolutions.larimarpharma.service;


import com.instantsolutions.larimarpharma.DTOs.DashboardStatsDto;
import com.instantsolutions.larimarpharma.DTOs.FieldExecutiveResponse;
import com.instantsolutions.larimarpharma.DTOs.ManagerRequestDto;
import com.instantsolutions.larimarpharma.DTOs.ManagerResponseDto;
import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.*;
import com.instantsolutions.larimarpharma.repository.ManagerRepository;
import com.instantsolutions.larimarpharma.utils.GeoUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final FEService feService;
    private final StockistRepository stockistRepository;
//    private final ManagerProfileRepository managerProfileRepository;
    private final ManagerVisitRepository managerVisitRepository;
    private final FieldExecutiveProfileRepository profileRepository;
    private final ManagerProfileRepository managerProfileRepository;
    private final FieldExecutiveProfileRepository fieldExecutiveProfileRepository;
//    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final VisitRepository visitRepository;
    private final LiquidationPlanRepository liquidationPlanRepository;


    public ManagerInfoResponseDto createManager(ManagerRequestDto dto) {

        System.out.println(dto);
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

         managerRepository.save(manager);
        ManagerProfile profile = ManagerProfile.builder()
                .manager(manager)
                .teamSize(0)
                .teamTargetAchieved(0.0)
                .teamComplianceRate(0.0)
                .totalApprovalsProcessed(0)
                .pendingApprovals(0)
                .build();
        managerProfileRepository.save(profile);

        Set<String> territories = manager.getFieldExecutives()
                .stream()
                .map(FieldExecutive::getTerritory)
                .filter(territory -> territory != null && !territory.isBlank())
                .collect(Collectors.toSet());

        return ManagerInfoResponseDto.builder()
                .id(manager.getId())
                .name(manager.getName())
                .email(manager.getEmail())
                .phone(manager.getPhone())
                .employeeCode(manager.getEmployeeCode())
                .department(manager.getDepartment())
                .designation(manager.getDesignation())
                .managedTerritories(territories)
                .build();


    }

    @Transactional
    public ManagerInfoResponseDto updateManager(Long id, UpdateManagerRequestDto dto) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Manager not found with id: " + id));

        manager.setName(dto.getName().toUpperCase());
        manager.setPhone(dto.getPhone());
        manager.setEmployeeCode(dto.getEmployeeCode().toUpperCase());
        manager.setDepartment(dto.getDepartment());
        manager.setDesignation(dto.getDesignation().toUpperCase());


         managerRepository.save(manager);

        Set<String> territories = manager.getFieldExecutives()
                .stream()
                .map(FieldExecutive::getTerritory)
                .filter(territory -> territory != null && !territory.isBlank())
                .collect(Collectors.toSet());

        return ManagerInfoResponseDto.builder()
                .id(manager.getId())
                .name(manager.getName())
                .email(manager.getEmail())
                .phone(manager.getPhone())
                .employeeCode(manager.getEmployeeCode())
                .department(manager.getDepartment())
                .designation(manager.getDesignation())
                .managedTerritories(territories)
                .build();

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

    public ManagerMonthlyFEProgressDto
    getMonthlyFEProgress(Long managerId, int month, int year) {

        ManagerTargetStatsDto dto =
                profileRepository.getManagerMonthlyTargets(
                        managerId, month, year
                );

        Double targetSet = (Double) dto.getTargetSet();
        Double targetAchieved = (Double) dto.getTargetAchieved();
        Long feCount = dto.getFeCount();

        double progress = 0.0;
        if (targetSet != null && targetSet > 0) {
            progress = (targetAchieved / targetSet) * 100;
        }

        return ManagerMonthlyFEProgressDto.builder()
                .managerId(managerId)
                .month(month)
                .year(year)
                .totalTargetSet(targetSet)
                .totalTargetAchieved(targetAchieved)
                .progressPercentage(
                        Math.round(progress * 100.0) / 100.0
                )
                .totalFieldExecutives(feCount.intValue())
                .build();
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
    public DashboardStatsDto getDashboardStats(Long managerId) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        // Get manager profile
        ManagerProfile profile = managerProfileRepository.findByManagerId(managerId)
                .orElseGet(() -> ManagerProfile.builder()
                        .teamSize(0)
                        .teamTargetAchieved(0.0)
                        .teamComplianceRate(0.0)
                        .build());

        LocalDate today = LocalDate.now();

        // First day of current month at 00:00
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();

        // Last day of current month at 23:59:59.999999999
        LocalDateTime endOfMonth = today
                .withDayOfMonth(today.lengthOfMonth())
                .atTime(LocalTime.MAX);

        int totalVisitsThisMonth = managerVisitRepository
                .countByManagerIdAndScheduledDateBetween(
                        managerId,
                        startOfMonth,
                        endOfMonth
                );
        int teamSize = fieldExecutiveRepository.countByManagerId(managerId);


        // Calculate trend (simplified - could be enhanced with historical data)
        double teamTargetProgress = profile.getTeamTargetAchieved() != null ?
                profile.getTeamTargetAchieved() : 0.0;

        String trend = calculateTrend(managerId);

        return DashboardStatsDto.builder()
                .totalVisits(totalVisitsThisMonth)
                .teamTargetProgress(getCurrentMonthTeamProgress(managerId))
                .totalMembers(teamSize)
                .trend(trend)
                .build();
    }

    public double getCurrentMonthTeamProgress(Long managerId) {

        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        Object result = visitRepository
                .getMonthlyVisitStatsByManager(managerId, startDate, endDate);

        Object[] stats = (Object[]) result;

        Long totalVisits = stats[0] != null ? ((Number) stats[0]).longValue() : 0L;
        Long completedVisits = stats[1] != null ? ((Number) stats[1]).longValue() : 0L;

        return totalVisits == 0
                ? 0.0
                : (completedVisits * 100.0) / totalVisits;
    }



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

    /**
     * Get today's schedule for a manager
     */
    @Transactional
    public List<ManagerVisitDto> getTodaySchedule(Long managerId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        List<ManagerVisit> visits = managerVisitRepository
                .findByManagerIdAndScheduledDateBetweenOrderByScheduledDateAsc(
                        managerId, startOfDay, endOfDay);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        return visits.stream()
                .map(visit -> {
                    String type = String.valueOf(visit.getVisitType());
                    String name = visit.getDoctorName();
                    String time = visit.getScheduledDate().format(timeFormatter);

                    return ManagerVisitDto.builder()
                            .name(name)
                            .time(time)
                            .type(type)
                            .feName(visit.getFieldExecutive().getName())
                            .scheduledDate(visit.getScheduledDate())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Get team performance data
     */
    public List<TeamMemberPerformanceDto> getTeamPerformance(Long managerId) {
        // Get all FEs under this manager
        List<FieldExecutive> fieldExecutives = fieldExecutiveRepository.findByManagerId(managerId);

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        return fieldExecutives.stream()
                .map(fe -> {
                    // Count visits for today for this FE
                    int totalVisitsToday = managerVisitRepository
                            .countByFieldExecutiveIdAndScheduledDateBetween(
                                    fe.getId(), startOfDay, endOfDay);

                    // Calculate target achieved (simplified - could be based on actual business logic)
//                    double targetAchieved = calculateTargetAchieved(fe.getId());
                    double targetAchieved = fieldExecutiveProfileRepository
                            .findByFieldExecutiveId(fe.getId())
                            .map(profile -> profile.getPrimaryTargetAchieved() != null
                                    ? profile.getPrimaryTargetAchieved()
                                    : 0.0)
                            .orElse(0.0);
                    return TeamMemberPerformanceDto.builder()
                            .id(fe.getId().toString())
                            .name(fe.getName())
                            .totalVisitsToday(totalVisitsToday)
                            .targetAchieved(targetAchieved)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private double calculateTargetAchieved(Long fieldExecutiveId) {

        LocalDate today = LocalDate.now();

        LocalDateTime startOfMonth =
                today.withDayOfMonth(1).atStartOfDay();

        LocalDateTime endOfMonth =
                today.withDayOfMonth(today.lengthOfMonth())
                        .atTime(LocalTime.MAX);


        int monthlyVisits =
                managerVisitRepository.countByFieldExecutiveIdAndScheduledDateBetween(
                        fieldExecutiveId,
                        startOfMonth,
                        endOfMonth
                );

        int monthlyTarget = 20;

        return Math.min((double) monthlyVisits / monthlyTarget * 100, 100);
    }



    public ManagerContactResponseDto getContactDetails(Long managerId) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        return  ManagerContactResponseDto.builder()
                .email(manager.getEmail()).
                emergencyContact(manager.getEmergencyContact()).
                phone(manager.getPhone()).name(manager.getName()).
                build();
    }

    public ManagerContactResponseDto updateContactDetails(Long managerId, ManagerContactUpdateRequestDto dto) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        if (dto.getPhone() != null) {
            manager.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            manager.setEmail(dto.getEmail());
        }
        if (dto.getEmergencyContact() != null) {
            manager.setEmergencyContact(dto.getEmergencyContact());
        }

        Manager updated = managerRepository.save(manager);

        return ManagerContactResponseDto.builder()
                .managerId(updated.getId())
                .phone(updated.getPhone())
                .email(updated.getEmail())
                .emergencyContact(updated.getEmergencyContact())
                .name(updated.getName())
                .build();
    }

    public List<FEContactResponseDto> getFEContactsUnderManager(Long managerId) {

        List<FieldExecutive> executives =
                fieldExecutiveRepository.findByManagerId(managerId);

        return executives.stream()
                .map(this::mapToContactDto)
                .toList();
    }

    private FEContactResponseDto mapToContactDto(FieldExecutive fe) {
        return FEContactResponseDto.builder()
                .feId(fe.getId())
                .name(fe.getName())
                .phone(fe.getPhone())
                .email(fe.getEmail())
                .emergencyContact(fe.getEmergencyContact())
                .build();
    }

    public List<ManagerContactResponseDto> getAllContactDetails() {

        return managerRepository.findAll()
                .stream()
                .map(fe -> ManagerContactResponseDto.builder()
                        .name(fe.getName())
                        .email(fe.getEmail())
                        .phone(fe.getPhone())
                        .emergencyContact(fe.getEmergencyContact())
                        .build()
                )
                .toList();
    }


    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getTeamMembers(Long managerId) {
        // Get all field executives for this manager
        List<FieldExecutive> fieldExecutives = fieldExecutiveRepository.findByManagerId(managerId);

        List<TeamMemberResponse> teamMembers = new ArrayList<>();

        for (FieldExecutive fe : fieldExecutives) {
            TeamMemberResponse member = mapToTeamMemberResponse(fe);
            teamMembers.add(member);
        }


        return teamMembers;
    }

    public List<FEBasicInfoDto> getFEBasicInfoUnderManager(Long managerId) {

        List<FieldExecutive> executives =
                fieldExecutiveRepository.findByManagerId(managerId);

        return executives.stream()
                .map(fe -> FEBasicInfoDto.builder()
                        .id(fe.getId())
                        .name(fe.getName())
                        .employeeCode(fe.getEmployeeCode())
                        .territory(
                                fe.getTerritory() != null
                                        ? fe.getTerritory()
                                        : null
                        )
                        .build())
                .toList();
    }

    @Transactional
    public List<ManagerLiquidationPlanDto>
    getCurrentMonthLiquidationPlansUnderManager(Long managerId) {

        LocalDate now = LocalDate.now();

        int month = now.getMonthValue();
        int year = now.getYear();

        List<LiquidationPlan> plans =
                liquidationPlanRepository
                        .findAllByManagerIdAndMonthAndYear(
                                managerId,
                                month,
                                year
                        );

        return plans.stream()
                .map(plan -> ManagerLiquidationPlanDto.builder()
                        .id(plan.getId())
                        .product(plan.getProduct().getName())
                        .quantity(plan.getAvailableUnits())
                        .doctor(plan.getDoctor().getName())
                        .targetLiquidation(plan.getTargetLiquidation())
                        .achievedUnits(plan.getAchievedUnits())
                        .marketName(plan.getMarketName())
                        .medicalShopName(plan.getMedicalShopName())
                        .status(plan.getManagerApprovalStatus().name())
                        .createdAt(plan.getCreatedAt())
                        .employeeId(plan.getFieldExecutive().getId())
                        .build()
                )
                .toList();
    }

    @Transactional
    public void assignStockistToManager(Long managerId, Long stockistId) {

        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        Stockist stockist = stockistRepository.findById(stockistId)
                .orElseThrow(() -> new ResourceNotFoundException("Stockist not found"));

        // avoid duplicate mapping
        if (!manager.getStockists().contains(stockist)) {
            manager.getStockists().add(stockist);
            stockist.getManagers().add(manager);
        }

        managerRepository.save(manager);
    }


    @Transactional(readOnly = true)
    public List<VisitResponse> getTodayVisitsForFieldExecutive(Long fieldExecutiveId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        List<Visit> visits = visitRepository.findTodaysVisits(fieldExecutiveId, startOfDay, endOfDay);

        return visits.stream()
                .map(this::mapToVisitResponse)
                .collect(Collectors.toList());
    }

    private TeamMemberResponse mapToTeamMemberResponse(FieldExecutive fieldExecutive) {
        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        // Get today's visits for this field executive
        List<Visit> todayVisits = visitRepository.findTodaysVisits(
                fieldExecutive.getId(),
                startOfDay,
                endOfDay
        );

        // Count completed visits
        long completedVisits = todayVisits.stream()
                .filter(v -> v.getStatus() == Visit.VisitStatus.COMPLETED)
                .count();

        long scheduledVisits = todayVisits.stream()
                .filter(v -> v.getStatus() == Visit.VisitStatus.SCHEDULED)
                .count();

        // Calculate target progress (assume 8 visits target per day)
        int target = Math.toIntExact(scheduledVisits);
        int targetProgress = target == 0 ? 0 : (int) ((completedVisits * 100) / target);

        // Get market (use first market if available)
        String market = fieldExecutive.getMarkets().isEmpty() ?
                "N/A" : fieldExecutive.getMarkets().get(0);

        // Map visits to VisitResponse
        List<VisitResponse> visitResponses = todayVisits.stream()
                .map(this::mapToVisitResponse)
                .collect(Collectors.toList());

        return TeamMemberResponse.builder()
                .id(fieldExecutive.getId())
                .name(fieldExecutive.getName())
                .email(fieldExecutive.getEmail())
                .market(market)
                .headquarters(fieldExecutive.getTerritory() != null ?
                        fieldExecutive.getTerritory() : "N/A")
                .todayVisitCount((int) scheduledVisits)
                .targetProgress(targetProgress)
                .visits(visitResponses)
                .build();
    }

    private VisitResponse mapToVisitResponse(Visit visit) {
        String doctorName = "N/A";
        String specialization = "N/A";
        String location = "N/A";

        // Extract doctor information
        if (visit.getDoctor() != null) {
            doctorName = visit.getDoctor().getName();
            specialization = visit.getDoctor().getDesignation() != null ?
                    visit.getDoctor().getDesignation() : "N/A";
        } else if (visit.getPharmacy() != null) {
            doctorName = visit.getPharmacy().getPharmacyName();
            specialization = "Pharmacy";
        } else if (visit.getStockist() != null) {
            doctorName = visit.getStockist().getName();
            specialization = "Stockist";
        }

        // Get location
        if (visit.getLocation() != null) {
            location = visit.getLocation();
        } else if (visit.getDoctor() != null && visit.getDoctor().getLocation() != null) {
            location = visit.getDoctor().getLocation();
        }

        // Format time
        String time = "N/A";
        if (visit.getScheduledDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            time = visit.getScheduledDate().toLocalTime().format(formatter);
        } else if (visit.getActualVisitTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            time = visit.getActualVisitTime().toLocalTime().format(formatter);
        }

        // Map status to frontend format
        String status;
        switch (visit.getStatus()) {
            case COMPLETED:
                status = "completed";
                break;
            case SCHEDULED:
            case APPROVED:
                status = "pending";
                break;
            case MISSED:
                status = "missed";
                break;
            case REJECTED:
            default:
                status = "pending";
                break;
        }

        return VisitResponse.builder()
                .id(visit.getId())
                .doctorName(doctorName)
                .specialization(specialization)
                .location(location)
                .time(time)
                .status(status)
                .build();
    }

    @Transactional
    public List<ManagerInfoResponseDto> getAllManagersInfo() {

        List<Manager> managers = managerRepository.findAll();

        return managers.stream().map(manager -> {

            // Extract territories from FEs
            Set<String> territories = manager.getFieldExecutives()
                    .stream()
                    .map(FieldExecutive::getTerritory)
                    .filter(territory -> territory != null && !territory.isBlank())
                    .collect(Collectors.toSet());

            return ManagerInfoResponseDto.builder()
                    .id(manager.getId())
                    .name(manager.getName())
                    .email(manager.getEmail())
                    .phone(manager.getPhone())
                    .employeeCode(manager.getEmployeeCode())
                    .department(manager.getDepartment())
                    .designation(manager.getDesignation())
                    .managedTerritories(territories)
                    .build();

        }).toList();
    }

}




