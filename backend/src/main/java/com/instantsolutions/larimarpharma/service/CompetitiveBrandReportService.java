package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.CompetitiveBrandReportRequestDto;
import com.instantsolutions.larimarpharma.DTOs.CompetitiveBrandReportResponseDto;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompetitiveBrandReportService {

    private final CompetitiveBrandReportRepository reportRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final ProductRepository productRepository;
    private final DoctorRepository doctorRepository;
    private final FileStorageService fileStorageService;

    /* ---------------- CREATE ---------------- */

    public CompetitiveBrandReportResponseDto create(
            CompetitiveBrandReportRequestDto dto,
            MultipartFile image
    ) {
        CompetitiveBrandReport report = CompetitiveBrandReport.builder()
                .fieldExecutive(getFieldExecutive(dto.getFieldExecutiveId()))
                .brandName(dto.getBrandName())
                .product(getProduct(dto.getProductId()))
                .productCategory(dto.getProductCategory())
                .doctor(getDoctor(dto.getDoctorId()))
                .hospitalName(dto.getHospitalName())
                .observations(dto.getObservations())
                .reportedDate(dto.getReportedDate())
                .build();

        if (image != null && !image.isEmpty()) {
            report.setImageUrl(fileStorageService.storeFile(image));
        }

        return toDto(reportRepository.save(report));
    }

    /* ---------------- UPDATE ---------------- */

    public CompetitiveBrandReportResponseDto update(
            Long id,
            CompetitiveBrandReportRequestDto dto,
            MultipartFile image
    ) {
        CompetitiveBrandReport report = getEntityById(id);

        report.setBrandName(dto.getBrandName());
        report.setProductCategory(dto.getProductCategory());
        report.setHospitalName(dto.getHospitalName());
        report.setObservations(dto.getObservations());

        if (dto.getFieldExecutiveId() != null)
            report.setFieldExecutive(getFieldExecutive(dto.getFieldExecutiveId()));

        if (dto.getProductId() != null)
            report.setProduct(getProduct(dto.getProductId()));

        if (dto.getDoctorId() != null)
            report.setDoctor(getDoctor(dto.getDoctorId()));

        if (image != null && !image.isEmpty()) {
            report.setImageUrl(fileStorageService.storeFile(image));
        }

        return toDto(reportRepository.save(report));
    }

    /* ---------------- READ ---------------- */

    @Transactional(readOnly = true)
    public CompetitiveBrandReportResponseDto getById(Long id) {
        return toDto(getEntityById(id));
    }

    @Transactional(readOnly = true)
    public List<CompetitiveBrandReportResponseDto> getAll() {
        return reportRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    /* ---------------- DELETE ---------------- */

    public void delete(Long id) {
        reportRepository.delete(getEntityById(id));
    }

    /* ---------------- MAPPER ---------------- */

    private CompetitiveBrandReportResponseDto toDto(CompetitiveBrandReport report) {
        return CompetitiveBrandReportResponseDto.builder()
                .id(report.getId())

                .fieldExecutiveId(report.getFieldExecutive().getId())
                .fieldExecutiveName(report.getFieldExecutive().getName())

                .brandName(report.getBrandName())

                .productId(report.getProduct() != null ? report.getProduct().getId() : null)
                .productName(report.getProduct() != null ? report.getProduct().getName() : null)
                .productCategory(report.getProductCategory())

                .doctorId(report.getDoctor() != null ? report.getDoctor().getId() : null)
                .doctorName(report.getDoctor() != null ? report.getDoctor().getName() : null)

                .hospitalName(report.getHospitalName())
                .observations(report.getObservations())
                .imageUrl(report.getImageUrl())

                .managerNotified(report.isManagerNotified())
                .reportedDate(report.getReportedDate())
                .createdAt(report.getCreatedAt())
                .build();
    }

    /* ---------------- HELPERS ---------------- */

    private CompetitiveBrandReport getEntityById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Competitive report not found: " + id)
                );
    }

    private FieldExecutive getFieldExecutive(Long id) {
        return fieldExecutiveRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("FieldExecutive not found: " + id)
                );
    }

    private Product getProduct(Long id) {
        if (id == null) return null;
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product not found: " + id)
                );
    }

    private Doctor getDoctor(Long id) {
        if (id == null) return null;
        return doctorRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Doctor not found: " + id)
                );
    }
}
