package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.AdminMessageRequestDto;
import com.instantsolutions.larimarpharma.entity.Admin;
import com.instantsolutions.larimarpharma.entity.AdminMessage;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.repository.AdminMessageRepository;
import com.instantsolutions.larimarpharma.repository.AdminRepository;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMessageService  {

    @Autowired
    AdminMessageRepository adminMessageRepository;
    @Autowired
    FieldExecutiveRepository fieldExecutiveRepository;
    @Autowired
     AdminRepository adminRepository;


    public AdminMessage sendMessageToSuperAdmin(
            Long feId,
            Long superAdminId,
            AdminMessageRequestDto dto) {

        FieldExecutive fe = fieldExecutiveRepository.findById(feId)
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        Admin superAdmin = adminRepository.findById(superAdminId)
                .orElseThrow(() -> new RuntimeException("Super Admin not found"));

        AdminMessage message = AdminMessage.builder()
                .fieldExecutive(fe)
                .superAdmin(superAdmin)
                .subject(dto.getSubject())
                .message(dto.getMessage())
                .build();

        return adminMessageRepository.save(message);
    }


    public List<AdminMessage> getMyMessages(Long feId) {
        return adminMessageRepository.findAllByFieldExecutiveIdOrderByCreatedAtDesc(feId);
    }
}
