package com.instantsolutions.larimarpharma.service;
import com.instantsolutions.larimarpharma.DTOs.SuperAdminContactResponseDto;
import com.instantsolutions.larimarpharma.entity.SuperAdmin;
import com.instantsolutions.larimarpharma.repository.SuperAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

    private final SuperAdminRepository superAdminRepository;

    public SuperAdminContactResponseDto getSuperAdminContact() {
        SuperAdmin superAdmin = superAdminRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Super Admin not found"));

        return SuperAdminContactResponseDto.builder()
                .id(superAdmin.getId())
                .name(superAdmin.getName())
                .email(superAdmin.getEmail())
                .phone(superAdmin.getPhone())
                .emergencyNumber(superAdmin.getEmergencyContact())
                .build();
    }
}

