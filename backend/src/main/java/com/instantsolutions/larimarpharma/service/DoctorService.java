package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.DoctorRequestDto;
import com.instantsolutions.larimarpharma.DTOs.DoctorResponseDto;
import com.instantsolutions.larimarpharma.entity.Doctor;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.repository.DoctorRepository;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    private final FieldExecutiveRepository fieldExecutiveRepository;

    public DoctorResponseDto create(DoctorRequestDto dto) {

        Doctor doctor = Doctor.builder()
                .name(dto.getName().toUpperCase())
                .category(dto.getCategory())
                .practiceType(dto.getPracticeType())
                .designation(dto.getDesignation().toUpperCase())
                .hospitalName(dto.getHospitalName())
                .location(dto.getLocation())
                .contactNumber(dto.getContactNumber())
                .doctorCode(dto.getDoctorCode().toUpperCase())
                .active(dto.isActive())
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);
        return DoctorResponseDto.fromEntity(savedDoctor);
    }

    public DoctorResponseDto update(Long id, DoctorRequestDto dto) {
        Doctor doctor = doctorRepository.findById(id).get();

        doctor.setName(dto.getName().toUpperCase());
        doctor.setCategory(dto.getCategory());
        doctor.setPracticeType(dto.getPracticeType());
        doctor.setDesignation(dto.getDesignation().toUpperCase());
        doctor.setHospitalName(dto.getHospitalName());
        doctor.setLocation(dto.getLocation());
        doctor.setContactNumber(dto.getContactNumber());
        doctor.setDoctorCode(dto.getDoctorCode().toUpperCase());
        doctor.setActive(dto.isActive());

        Doctor savedDoctor = doctorRepository.save(doctor);
        return DoctorResponseDto.fromEntity(savedDoctor);
    }

    public DoctorResponseDto getById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return DoctorResponseDto.fromEntity(doctor);
    }

    public List<DoctorResponseDto> getAll() {
        return doctorRepository.findAll()
                .stream()
                .map(DoctorResponseDto::fromEntity)
                .toList();
    }

    public void delete(Long id) {
        Optional<Doctor> doctor = doctorRepository.findById(id);
        if (doctor.isPresent()){
            doctor.get().setActive(false);   // SOFT DELETE
            doctorRepository.save(doctor.get());
        }
    }

    public void activate(Long id){
        Optional<Doctor> doctor = doctorRepository.findById(id);
        if(doctor.isPresent()){
            doctor.get().setActive(true);
            doctorRepository.save(doctor.get());
        }

    }

    public List<DoctorResponseDto> getAllActive() {
        return doctorRepository.findByActiveTrue()
                .stream()
                .map(DoctorResponseDto::fromEntity)
                .toList();
    }

    public List<DoctorResponseDto> getDoctorAssignedForFe(Long feId) {
        return doctorRepository.findByFieldExecutiveId(feId)
                .stream()
                .map(DoctorResponseDto::fromEntity)
                .toList();
    }

    public List<Doctor> getInactive() {
        return doctorRepository.findByActiveFalse();
    }

    @Transactional
    public DoctorResponseDto assignDoctorToFE(Long doctorId, Long feId) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        FieldExecutive fe = fieldExecutiveRepository.findById(feId)
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        doctor.setFieldExecutive(fe);

         doctorRepository.save(doctor);
         return DoctorResponseDto.fromEntity(doctor);
    }
}
