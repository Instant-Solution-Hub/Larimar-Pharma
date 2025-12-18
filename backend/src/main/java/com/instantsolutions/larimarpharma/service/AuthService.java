package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.LoginResponseDto;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.exceptions.AuthenticationException;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public LoginResponseDto authenticate(String email, String password) {

        Optional<FieldExecutive> feOptional = fieldExecutiveRepository.findByEmail(email);
        if (feOptional.isPresent() && passwordMatches(feOptional.get(), password)) {
            FieldExecutive fieldExecutive = feOptional.get();
            return new LoginResponseDto(fieldExecutive.getId(),fieldExecutive.getEmail(),fieldExecutive.getName()
            , fieldExecutive.getPhone(),"FE", fieldExecutive.isActive(), fieldExecutive.getEmergencyContact(),
                    fieldExecutive.getCreatedAt().toString());
        }

        Optional<Manager> managerOptional = managerRepository.findByEmail(email);
        if (managerOptional.isPresent() && passwordMatches(managerOptional.get(), password)) {
            Manager manager = managerOptional.get();
            return new LoginResponseDto(manager.getId(),manager.getEmail(),manager.getName()
                    , manager.getPhone(),"Manager", manager.isActive(), manager.getEmergencyContact(),
                    manager.getCreatedAt().toString());
        }

        Optional<Admin> adminOptional = adminRepository.findByEmail(email);
        if (adminOptional.isPresent() && passwordMatches(adminOptional.get(), password)) {
            Admin admin = adminOptional.get();
            return new LoginResponseDto(admin.getId(),admin.getEmail(),admin.getName()
                    , admin.getPhone(),"Admin", admin.isActive(), admin.getEmergencyContact(),
                    admin.getCreatedAt().toString());
        }

        Optional<SuperAdmin> superAdminOptional = superAdminRepository.findByEmail(email);
        if (superAdminOptional.isPresent() && passwordMatches(superAdminOptional.get(), password)) {
            SuperAdmin superAdmin = superAdminOptional.get();
            return new LoginResponseDto(superAdmin.getId(),superAdmin.getEmail(),superAdmin.getName()
                    , superAdmin.getPhone(),"SuperAdmin", superAdmin.isActive(), superAdmin.getEmergencyContact(),
                    superAdmin.getCreatedAt().toString());

        }

        throw new AuthenticationException("Invalid credentials");
    }

    private boolean passwordMatches(BaseUser user, String rawPassword) {
        return user.getPassword() != null
                && user.getPassword().equals(rawPassword);
    }
}

