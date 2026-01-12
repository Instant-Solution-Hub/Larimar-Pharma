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
                .companyName(dto.getCompanyName())
                .productName(dto.getProductName())
                .productCategory(dto.getProductCategory())
                .source(dto.getSource())
                .designation(dto.getDesignation())
                .observations(dto.getObservations())
                .build();

        if (image != null && !image.isEmpty()) {
            report.setImageUrl(fileStorageService.storeFile(image, "competitive-reports"));
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
        report.setCompanyName(dto.getCompanyName());
        report.setProductName(dto.getProductName());
        report.setProductCategory(dto.getProductCategory());
        report.setSource(dto.getSource());
        report.setDesignation(dto.getDesignation());
        report.setObservations(dto.getObservations());

        if (image != null && !image.isEmpty()) {
            report.setImageUrl(fileStorageService.storeFile(image, "competitive-reports"));
        }
        else report.setImageUrl(null);
        reportRepository.save(report);

        return toDto(report);


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

    @Transactional(readOnly = true)
    public List<CompetitiveBrandReportResponseDto> getAllByFieldExecutive(Long feId) {

        // Optional safety check (recommended)
        fieldExecutiveRepository.findById(feId)
                .orElseThrow(() ->
                        new EntityNotFoundException("FieldExecutive not found: " + feId)
                );

        return reportRepository
                .findByFieldExecutiveIdOrderByCreatedAtDesc(feId)
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
                .companyName(report.getCompanyName())

                .productName(report.getProductName())
                .productCategory(report.getProductCategory())
                .source(report.getSource())

                .designation(report.getDesignation())
                .observations(report.getObservations())
                .imageUrl(report.getImageUrl())

                .managerNotified(report.isManagerNotified())
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

    private FieldExecutive
    getFieldExecutive(Long id) {
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
