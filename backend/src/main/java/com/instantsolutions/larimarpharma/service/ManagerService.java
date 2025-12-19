package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.ManagerRequestDto;
import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.repository.ManagerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;

    public Manager createManager(ManagerRequestDto dto) {
        Manager manager = Manager.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .active(dto.isActive())
                .employeeCode(dto.getEmployeeCode())
                .department(dto.getDepartment())
                .designation(dto.getDesignation())
                .managedTerritories(dto.getManagedTerritories())
                .build();

        return managerRepository.save(manager);
    }

    public Manager updateManager(Long id, ManagerRequestDto dto) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Manager not found with id: " + id));

        manager.setName(dto.getName());
        manager.setEmail(dto.getEmail());
        manager.setPhone(dto.getPhone());
        manager.setActive(dto.isActive());
        manager.setEmployeeCode(dto.getEmployeeCode());
        manager.setDepartment(dto.getDepartment());
        manager.setDesignation(dto.getDesignation());
        manager.setManagedTerritories(dto.getManagedTerritories());

        return managerRepository.save(manager);
    }


    public Manager getManagerById(Long id) {
        return managerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Manager not found with id: " + id));
    }

    public List<Manager> getAllManagers() {
        return managerRepository.findAll();
    }

    public void deleteManager(Long id) {
        if (!managerRepository.existsById(id)) {
            throw new EntityNotFoundException("Manager not found with id: " + id);
        }
        managerRepository.deleteById(id);
    }
}
