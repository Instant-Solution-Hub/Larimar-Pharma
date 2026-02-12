package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.LoginResponseDto;
import com.instantsolutions.larimarpharma.DTOs.UserIdentityDto;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.exceptions.AuthenticationException;
import com.instantsolutions.larimarpharma.exceptions.PortalLockedException;
import com.instantsolutions.larimarpharma.repository.AdminRepository;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.ManagerRepository;
import com.instantsolutions.larimarpharma.repository.SuperAdminRepository;
import com.instantsolutions.larimarpharma.security.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private FieldExecutiveRepository fieldExecutiveRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private PortalLockService portalLockService;

    public LoginResponseDto authenticate(String email, String password) {

        BaseUser user = null;
        String role = null;

        // ================= FIELD EXECUTIVE =================
        Optional<FieldExecutive> feOptional = fieldExecutiveRepository.findByEmail(email);
        if (feOptional.isPresent() && passwordMatches(feOptional.get(), password)) {
            FieldExecutive fe = feOptional.get();

            UserIdentityDto userIdentityDto = UserIdentityDto.builder()
                    .userType("FIELD_EXECUTIVE")
                    .userId(fe.getId())
                    .build();

            boolean isLocked = portalLockService.isPortalLocked(userIdentityDto);

            if (isLocked) {
                throw new PortalLockedException("Portal is locked", userIdentityDto);

            }

            user = fe;
            role = "FE";
        }

        // ================= MANAGER =================
        if (user == null) {
            Optional<Manager> managerOptional = managerRepository.findByEmail(email);
            if (managerOptional.isPresent() && passwordMatches(managerOptional.get(), password)) {

                Manager manager = managerOptional.get();

                UserIdentityDto userIdentityDto = UserIdentityDto.builder()
                        .userType("MANAGER")
                        .userId(manager.getId())
                        .build();

                boolean isLocked = portalLockService.isPortalLocked(userIdentityDto);

                if (isLocked) {
                    throw new PortalLockedException("Portal is locked", userIdentityDto);
                }

                user = manager;
                role = "Manager";
            }
        }

        // ================= ADMIN =================
        if (user == null) {
            Optional<Admin> adminOptional = adminRepository.findByEmail(email);
            if (adminOptional.isPresent() && passwordMatches(adminOptional.get(), password)) {
                user = adminOptional.get();
                role = "Admin";
            }
        }

        // ================= SUPER ADMIN =================
        if (user == null) {
            Optional<SuperAdmin> superAdminOptional = superAdminRepository.findByEmail(email);
            if (superAdminOptional.isPresent() && passwordMatches(superAdminOptional.get(), password)) {
                user = superAdminOptional.get();
                role = "SUPER_ADMIN";
            }
        }

        if (user == null) {
            throw new AuthenticationException("Invalid credentials");
        }

        // ===== SECURITY CONTEXT =====
        AppUserDetails userDetails = new AppUserDetails(user);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new LoginResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                role,
                user.isActive(),
                user.getEmergencyContact(),
                user.getCreatedAt().toString()
        );
    }

    private boolean passwordMatches(BaseUser user, String rawPassword) {
        return user.getPassword() != null
                && user.getPassword().equals(rawPassword);
    }
}

