package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.ManagerProfileStatsDto;
import com.instantsolutions.larimarpharma.DTOs.ManagerTargetStatsDto;
import com.instantsolutions.larimarpharma.entity.ManagerProfile;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveProfileRepository;
import com.instantsolutions.larimarpharma.repository.ManagerProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ManagerProfileService {

    private final ManagerProfileRepository managerProfileRepository;
    private final FieldExecutiveProfileRepository feProfileRepository;
    private final TerritoryMonthlyTargetService territoryMonthlyTargetService;

    public ManagerProfileStatsDto getManagerProfileStats(Long managerId) {

        ManagerProfile profile = managerProfileRepository
                .findByManagerId(managerId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Manager profile not found"));

        LocalDate today = LocalDate.now();

        ManagerTargetStatsDto dto = feProfileRepository.getManagerMonthlyTargets(
                managerId,
                today.getMonthValue(),
                today.getYear()
        );
        Double getTotal = territoryMonthlyTargetService.getTotalAchievedForManager(managerId, today.getMonthValue(), today.getYear());



        return ManagerProfileStatsDto.builder()
                .targetSet(dto.getTargetSet())
                .targetAchieved(getTotal)
                .casualLeaves(profile.getCasualLeaves())
                .approvedCasualLeaves(profile.getApprovedCasualLeaves())
                .sickLeaves(profile.getSickLeaves())
                .approvedSickLeaves(profile.getApprovedSickLeaves())
                .build();
    }
}
