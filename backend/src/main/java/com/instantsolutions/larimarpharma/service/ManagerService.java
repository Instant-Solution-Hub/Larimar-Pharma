package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.entity.ManagerProfile;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.ManagerProfileRepository;
import com.instantsolutions.larimarpharma.repository.ManagerRepository;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final FieldExecutiveProfileRepository profileRepository;
    private final ManagerProfileRepository managerProfileRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;

    public Manager createManager(ManagerRequestDto dto) {
        Manager manager = Manager.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .phone(dto.getPhone())
                .active(dto.isActive())
                .employeeCode(dto.getEmployeeCode())
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

        return manager;
    }

    public Manager updateManager(Long id, ManagerRequestDto dto) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Manager not found with id: " + id));

        manager.setName(dto.getName());
        manager.setEmail(dto.getEmail());
        manager.setPassword(dto.getPassword());
        manager.setPhone(dto.getPhone());
        manager.setActive(dto.isActive());
        manager.setEmployeeCode(dto.getEmployeeCode());
        manager.setDepartment(dto.getDepartment());
        manager.setDesignation(dto.getDesignation());
        manager.setManagedTerritories(dto.getManagedTerritories());

        return managerRepository.save(manager);
    }


    public ManagerResponseDto getManagerById(Long id) {
        Manager manager = managerRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Manager not found with id: " + id));
        return toManagerResponseDto(manager);
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
    }

