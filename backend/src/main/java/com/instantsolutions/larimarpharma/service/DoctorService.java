package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.DoctorRequestDto;
import com.instantsolutions.larimarpharma.entity.Doctor;
import com.instantsolutions.larimarpharma.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public Doctor create(DoctorRequestDto dto) {
        Doctor doctor = Doctor.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .practiceType(dto.getPracticeType())
                .designation(dto.getDesignation())
                .hospitalName(dto.getHospitalName())
                .location(dto.getLocation())
                .contactNumber(dto.getContactNumber())
                .doctorCode(dto.getDoctorCode())
                .active(dto.isActive())
                .build();

        return doctorRepository.save(doctor);
    }

    public Doctor update(Long id, DoctorRequestDto dto) {
        Doctor doctor = getById(id);

        doctor.setName(dto.getName());
        doctor.setCategory(dto.getCategory());
        doctor.setPracticeType(dto.getPracticeType());
        doctor.setDesignation(dto.getDesignation());
        doctor.setHospitalName(dto.getHospitalName());
        doctor.setLocation(dto.getLocation());
        doctor.setContactNumber(dto.getContactNumber());
        doctor.setDoctorCode(dto.getDoctorCode());
        doctor.setActive(dto.isActive());

        return doctorRepository.save(doctor);
    }

    public Doctor getById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public List<Doctor> getAll() {
        return doctorRepository.findAll();
    }

    public void delete(Long id) {
        Doctor doctor = getById(id);
        doctor.setActive(false);   // SOFT DELETE
        doctorRepository.save(doctor);
    }

    public List<Doctor> getAllActive() {
        return doctorRepository.findByActiveTrue();
    }

    public List<Doctor> getInactive() {
        return doctorRepository.findByActiveFalse();
    }
}
