package com.instantsolutions.larimarpharma.service;
import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.entity.Admin;
import com.instantsolutions.larimarpharma.entity.AdminProfile;
import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.repository.AdminProfileRepository;
import com.instantsolutions.larimarpharma.repository.AdminRepository;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final FieldExecutiveProfileRepository feProfileRepository;
    private final AdminProfileRepository adminProfileRepository;
    private final TerritoryMonthlyTargetService territoryMonthlyTargetService;

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
}

