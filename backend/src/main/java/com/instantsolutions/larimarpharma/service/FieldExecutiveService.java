package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.FEUpdateContactDto;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.exceptions.BadRequestException;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FieldExecutiveService {

    private final FieldExecutiveRepository fieldExecutiveRepository;

    @Transactional
    public FieldExecutive updateContactDetails(
            Long feId,
            FEUpdateContactDto dto
    ) {
        FieldExecutive fe = fieldExecutiveRepository.findById(feId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Field Executive not found"));

        // Email uniqueness check
        if (!fe.getEmail().equals(dto.getEmail())
                && fieldExecutiveRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already in use");
        }

        if (dto.getPhone() != null &&
                dto.getPhone().equals(dto.getEmergencyContact())) {
            throw new BadRequestException(
                    "Phone number and emergency contact cannot be same"
            );
        }


        fe.setEmail(dto.getEmail());
        fe.setPhone(dto.getPhone());
        fe.setEmergencyContact(dto.getEmergencyContact());

        return fieldExecutiveRepository.save(fe);
    }
}
