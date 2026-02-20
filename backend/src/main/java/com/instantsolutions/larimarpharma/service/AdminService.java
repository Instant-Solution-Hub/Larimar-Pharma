package com.instantsolutions.larimarpharma.service;
import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
//import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.IntStream;

@Service

@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final FieldExecutiveProfileRepository feProfileRepository;
    private final AdminProfileRepository adminProfileRepository;

    private final TerritoryMonthlyTargetService territoryMonthlyTargetService;

    private final VisitRepository visitRepository;
    private final ManagerVisitRepository managerVisitRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;
    private  final ManagerRepository managerRepository;


    public AdminContactResponseDto getAdminContact() {
        Admin admin = adminRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        return AdminContactResponseDto.builder()
                .adminId(admin.getId())
                .name(admin.getName())
                .email(admin.getEmail())
                .phone(admin.getPhone())
                .emergencyContact(admin.getEmergencyContact())
                .build();
    }



    public AdminDashboardStatsDto getAdminDashboardStats(Long adminId) {

        LocalDate today = LocalDate.now();

        AdminTargetStatsDto targetStats =
                feProfileRepository.getOverallMonthlyTargets(
                        today.getMonthValue(),
                        today.getYear()
                );

        AdminProfile adminProfile = adminProfileRepository
                .findByAdminId(adminId)
                .orElseThrow(() -> new RuntimeException("Admin profile not found"));

        Double totalAchieved = territoryMonthlyTargetService.getTotalAchievedForAllTerritories(today.getMonthValue(), today.getYear());

        return AdminDashboardStatsDto.builder()
                .targetSet(targetStats.getTargetSet())
                .targetAchieved(totalAchieved)
                .casualLeaves(adminProfile.getCasualLeaves())
                .approvedCasualLeaves(adminProfile.getApprovedCasualLeaves())
                .sickLeaves(adminProfile.getSickLeaves())
                .approvedSickLeaves(adminProfile.getApprovedSickLeaves())
                .build();
    }

    public AdminContactResponseDto updateContactDetails(Long adminId, ManagerContactUpdateRequestDto dto) {

            Admin admin = adminRepository.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Field Executive not found"));

            if (dto.getPhone() != null) {
                admin.setPhone(dto.getPhone());
            }
            if (dto.getEmail() != null) {
                admin.setEmail(dto.getEmail());
            }
            if (dto.getEmergencyContact() != null) {
                admin.setEmergencyContact(dto.getEmergencyContact());
            }

            Admin updated = adminRepository.save(admin);

            return AdminContactResponseDto.builder()
                    .adminId(updated.getId())
                    .phone(updated.getPhone())
                    .email(updated.getEmail())
                    .emergencyContact(updated.getEmergencyContact())
                    .name(updated.getName())
                    .build();

    }


//    public List<DailyVisitStatsDto> getDailyVisitStatsForFieldExecutive(int month, int year) {
//        LocalDate startDate = LocalDate.of(year, month, 1);
//        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
//
//        List<Visit> visits = visitRepository.findByVisitDateBetween(startDate, endDate);
//        return aggregateDailyStats(visits, startDate);
//    }

//    public List<DailyVisitStatsDto> getDailyVisitStatsForManager(int month, int year) {
//        LocalDate startDate = LocalDate.of(year, month, 1);
//        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
//
//        List<ManagerVisit> managerVisits = managerVisitRepository.findByVisitDateBetween(startDate, endDate);
//        return aggregateManagerDailyStats(managerVisits, startDate);
//    }

//    private List<DailyVisitStatsDto> aggregateDailyStats(List<Visit> visits, LocalDate startDate) {
//        Map<LocalDate, DailyVisitStatsDto> statsMap = new LinkedHashMap<>();
//
//        // Initialize all days of the month
//        IntStream.rangeClosed(1, startDate.lengthOfMonth())
//                .forEach(day -> {
//                    LocalDate date = startDate.withDayOfMonth(day);
//                    statsMap.put(date, new DailyVisitStatsDto(
//                            date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
//                            0L, 0L, 0L
//                    ));
//                });
//
//        // Aggregate actual visit data
//        visits.forEach(visit -> {
//            LocalDate date = visit.getVisitDate();
//            DailyVisitStatsDto stats = statsMap.get(date);
//
//            if (stats != null) {
//                switch (visit.getStatus()) {
//                    case COMPLETED:
//                        stats.setCompleted(stats.getCompleted() + 1);
//                        break;
//                    case MISSED:
//                        stats.setMissed(stats.getMissed() + 1);
//                        break;
//                    case SCHEDULED:
//                    case APPROVED:
//                        stats.setPending(stats.getPending() + 1);
//                        break;
//                    default:
//                        // REJECTED and others don't count
//                        break;
//                }
//            }
//        });
//
//        return new ArrayList<>(statsMap.values());
//    }

//    private List<DailyVisitStatsDto> aggregateManagerDailyStats(List<ManagerVisit> managerVisits, LocalDate startDate) {
//        Map<LocalDate, DailyVisitStatsDto> statsMap = new LinkedHashMap<>();
//
//        // Initialize all days of the month
//        IntStream.rangeClosed(1, startDate.lengthOfMonth())
//                .forEach(day -> {
//                    LocalDate date = startDate.withDayOfMonth(day);
//                    statsMap.put(date, new DailyVisitStatsDto(
//                            date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
//                            0L, 0L, 0L
//                    ));
//                });
//
//        // Aggregate actual visit data
//        managerVisits.forEach(visit -> {
//            LocalDate date = visit.getVisitDate();
//            DailyVisitStatsDto stats = statsMap.get(date);
//
//            if (stats != null) {
//                switch (visit.getStatus()) {
//                    case COMPLETED:
//                        stats.setCompleted(stats.getCompleted() + 1);
//                        break;
//                    case MISSED:
//                        stats.setMissed(stats.getMissed() + 1);
//                        break;
//                    case SCHEDULED:
//                    case APPROVED:
//                        stats.setPending(stats.getPending() + 1);
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//
//        return new ArrayList<>(statsMap.values());
//    }

    public List<WeeklyVisitStatsDto> getWeeklyVisitStatsForFieldExecutive(int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Visit> visits = visitRepository.findByVisitDateBetween(startDate, endDate);
        return aggregateWeeklyStats(visits, startDate, endDate);
    }

    public List<WeeklyVisitStatsDto> getWeeklyVisitStatsForManager(int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<ManagerVisit> managerVisits = managerVisitRepository.findByVisitDateBetween(startDate, endDate);
        return aggregateManagerWeeklyStats(managerVisits, startDate, endDate);
    }

    private List<WeeklyVisitStatsDto> aggregateWeeklyStats(List<Visit> visits, LocalDate startDate, LocalDate endDate) {
        Map<Integer, WeeklyVisitStatsDto> weeklyStatsMap = new LinkedHashMap<>();

        // Get the week numbers for the month
        Set<Integer> weeksInMonth = new TreeSet<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            int weekNumber = getWeekOfMonth(currentDate);
            weeksInMonth.add(weekNumber);
            currentDate = currentDate.plusDays(1);
        }

        // Initialize all weeks of the month
        weeksInMonth.forEach(weekNum ->
                weeklyStatsMap.put(weekNum, new WeeklyVisitStatsDto(
                        "Week " + weekNum,
                        0L, 0L, 0L
                ))
        );

        // Aggregate actual visit data by week
        visits.forEach(visit -> {
            LocalDate date = visit.getVisitDate();
            int weekNumber = getWeekOfMonth(date);
            WeeklyVisitStatsDto stats = weeklyStatsMap.get(weekNumber);

            if (stats != null) {
                switch (visit.getStatus()) {
                    case COMPLETED:
                        stats.setCompleted(stats.getCompleted() + 1);
                        break;
                    case MISSED:
                        stats.setMissed(stats.getMissed() + 1);
                        break;
                    case SCHEDULED:
                    case APPROVED:
                        stats.setPending(stats.getPending() + 1);
                        break;
                    default:
                        break;
                }
            }
        });

        return new ArrayList<>(weeklyStatsMap.values());
    }

    private List<WeeklyVisitStatsDto> aggregateManagerWeeklyStats(List<ManagerVisit> managerVisits, LocalDate startDate, LocalDate endDate) {
        Map<Integer, WeeklyVisitStatsDto> weeklyStatsMap = new LinkedHashMap<>();

        // Get the week numbers for the month
        Set<Integer> weeksInMonth = new TreeSet<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            int weekNumber = getWeekOfMonth(currentDate);
            weeksInMonth.add(weekNumber);
            currentDate = currentDate.plusDays(1);
        }

        // Initialize all weeks of the month
        weeksInMonth.forEach(weekNum ->
                weeklyStatsMap.put(weekNum, new WeeklyVisitStatsDto(
                        "Week " + weekNum,
                        0L, 0L, 0L
                ))
        );

        // Aggregate actual visit data by week
        managerVisits.forEach(visit -> {
            LocalDate date = visit.getVisitDate();
            int weekNumber = getWeekOfMonth(date);
            WeeklyVisitStatsDto stats = weeklyStatsMap.get(weekNumber);

            if (stats != null) {
                switch (visit.getStatus()) {
                    case COMPLETED:
                        stats.setCompleted(stats.getCompleted() + 1);
                        break;
                    case MISSED:
                        stats.setMissed(stats.getMissed() + 1);
                        break;
                    case SCHEDULED:
                    case APPROVED:
                        stats.setPending(stats.getPending() + 1);
                        break;
                    default:
                        break;
                }
            }
        });

        return new ArrayList<>(weeklyStatsMap.values());
    }

    private int getWeekOfMonth(LocalDate date) {
        // Calculate week of month (1-5)
        int dayOfMonth = date.getDayOfMonth();
        return ((dayOfMonth - 1) / 7) + 1;
    }

    public AdminDashboardCounts getCounts(){
        Long feCount = fieldExecutiveRepository.count();
        Long managerCount = managerRepository.count();
        return AdminDashboardCounts.builder()
                .totalUsers(feCount+managerCount)
                .totalFieldExecutives(feCount)
                .totalManagers(managerCount)
                .build();
    }
}

