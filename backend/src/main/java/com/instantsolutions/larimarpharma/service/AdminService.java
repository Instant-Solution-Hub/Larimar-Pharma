package com.instantsolutions.larimarpharma.service;
import com.instantsolutions.larimarpharma.DTOs.AdminContactResponseDto;
import com.instantsolutions.larimarpharma.entity.Admin;
import com.instantsolutions.larimarpharma.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminContactResponseDto getAdminContact() {
        Admin admin = adminRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        return AdminContactResponseDto.builder()
                .id(admin.getId())
                .name(admin.getName())
                .email(admin.getEmail())
                .phone(admin.getPhone())
                .emergencyNumber(admin.getEmergencyContact())
                .build();
    }
}

