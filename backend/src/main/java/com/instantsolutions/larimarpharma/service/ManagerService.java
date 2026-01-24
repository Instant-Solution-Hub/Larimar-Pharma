package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.FieldExecutiveResponse;
import com.instantsolutions.larimarpharma.DTOs.ManagerRequestDto;
import com.instantsolutions.larimarpharma.DTOs.ManagerResponseDto;
import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.repository.ManagerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;

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

}
