package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.PharmacyRequestDto;
import com.instantsolutions.larimarpharma.DTOs.PharmacyResponseDto;
import com.instantsolutions.larimarpharma.entity.Doctor;
import com.instantsolutions.larimarpharma.entity.Pharmacy;
import com.instantsolutions.larimarpharma.repository.DoctorRepository;
import com.instantsolutions.larimarpharma.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;
    private final DoctorRepository doctorRepository;

    /* CREATE */
    public PharmacyResponseDto create(PharmacyRequestDto dto) {
        Pharmacy pharmacy = Pharmacy.builder()
                .pharmacyName(dto.getPharmacyName().toUpperCase())
                .location(dto.getLocation())
                .contactPerson(dto.getContactPerson())
                .contactNumber(dto.getContactNumber())
                .doctor(getDoctor(dto.getDoctorId()))
                .build();

        return mapToResponse(pharmacyRepository.save(pharmacy));
    }

    /* READ BY ID */
    public PharmacyResponseDto getById(Long id) {
        return mapToResponse(getPharmacy(id));
    }

    /* READ ALL */
    public List<PharmacyResponseDto> getAll() {
        return pharmacyRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<PharmacyResponseDto> getAllByFE(Long fieldExecutiveId) {
        return pharmacyRepository.findByFieldExecutiveId(fieldExecutiveId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /* UPDATE */
    public PharmacyResponseDto update(Long id, PharmacyRequestDto dto) {
        Pharmacy pharmacy = getPharmacy(id);

        pharmacy.setPharmacyName(dto.getPharmacyName().toUpperCase());
        pharmacy.setLocation(dto.getLocation());
        pharmacy.setContactPerson(dto.getContactPerson());
        pharmacy.setContactNumber(dto.getContactNumber());
        pharmacy.setDoctor(getDoctor(dto.getDoctorId()));

        return mapToResponse(pharmacyRepository.save(pharmacy));
    }

    /* DELETE */
    public void delete(Long id) {
        pharmacyRepository.delete(getPharmacy(id));
    }

    /* ================= Helpers ================= */

    private Pharmacy getPharmacy(Long id) {
        return pharmacyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pharmacy not found with id: " + id));
    }

    private Doctor getDoctor(Long doctorId) {
        if (doctorId == null) return null;

        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
    }

    private PharmacyResponseDto mapToResponse(Pharmacy pharmacy) {
        return PharmacyResponseDto.builder()
                .id(pharmacy.getId())
                .pharmacyName(pharmacy.getPharmacyName())
                .location(pharmacy.getLocation())
                .contactPerson(pharmacy.getContactPerson())
                .contactNumber(pharmacy.getContactNumber())
                .doctorId(
                        pharmacy.getDoctor() != null
                                ? pharmacy.getDoctor().getId()
                                : null
                )
                .doctorName(
                        pharmacy.getDoctor() != null
                                ? pharmacy.getDoctor().getName()
                                : null
                )
                .createdAt(pharmacy.getCreatedAt())
                .updatedAt(pharmacy.getUpdatedAt())
                .build();
    }
}
