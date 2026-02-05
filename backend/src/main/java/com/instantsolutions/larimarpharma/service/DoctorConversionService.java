package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.DoctorConversionRequestDto;
import com.instantsolutions.larimarpharma.DTOs.DoctorConversionResponseDto;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.repository.DoctorConversionRepository;
import com.instantsolutions.larimarpharma.repository.DoctorRepository;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorConversionService {

    private final DoctorConversionRepository doctorConversionRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final DoctorRepository doctorRepository;
    private final ProductRepository productRepository;



    @Transactional
    public DoctorConversionResponseDto addDoctorConversion(DoctorConversionRequestDto request) {

        FieldExecutive fieldExecutive = fieldExecutiveRepository.findById(request.getFieldExecutiveId())
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        DoctorConversion conversion = DoctorConversion.builder()
                .fieldExecutive(fieldExecutive)
                .doctor(doctor)
                .product(product)
                .build();

        DoctorConversion saved = doctorConversionRepository.save(conversion);

        return mapToDto(saved);
    }

    @Transactional
    public List<DoctorConversionResponseDto> getCurrentMonthConversions() {

        YearMonth yearMonth = YearMonth.now();

        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<DoctorConversion> conversions =
                doctorConversionRepository.findAll();

        return conversions.stream()
                .map(this::mapToDto)
                .toList();
    }

    public void deleteDoctorConversion(Long feId, Long conversionId) {

        DoctorConversion conversion =
                doctorConversionRepository
                        .findByIdAndFieldExecutiveId(conversionId, feId)
                        .orElseThrow(() -> new RuntimeException("Doctor conversion not found"));

        doctorConversionRepository.delete(conversion);
    }


    private DoctorConversionResponseDto mapToDto(DoctorConversion conversion) {

        return DoctorConversionResponseDto.builder()
                .id(conversion.getId())
                .fieldExecutiveId(conversion.getFieldExecutive().getId())
                .fieldExecutiveName(conversion.getFieldExecutive().getName())
                .managerId(conversion.getFieldExecutive().getManager() != null
                        ? conversion.getFieldExecutive().getManager().getId() : null)
                .managerName(conversion.getFieldExecutive().getManager() != null
                        ? conversion.getFieldExecutive().getManager().getName() : null)
                .doctorId(conversion.getDoctor().getId())
                .doctorName(conversion.getDoctor().getName())
                .hospitalName(conversion.getDoctor().getHospitalName())
                .productId(conversion.getProduct().getId())
                .productName(conversion.getProduct().getName())
                .createdAt(java.sql.Timestamp.valueOf(conversion.getCreatedAt()))
                .build();
    }
}

