package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.CompetitiveBrandReportRequestDto;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitiveBrandReportService {

    private final CompetitiveBrandReportRepository reportRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final ProductRepository productRepository;
    private final DoctorRepository doctorRepository;
    private final FileStorageService fileStorageService;

    public CompetitiveBrandReport create(
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
                .managerNotified(dto.isManagerNotified())
                .reportedDate(dto.getReportedDate())
                .build();

        if (image != null && !image.isEmpty()) {
            report.setImageUrl(fileStorageService.storeFile(image));
        }

        return reportRepository.save(report);
    }

    public CompetitiveBrandReport update(
            Long id,
            CompetitiveBrandReportRequestDto dto,
            MultipartFile image
    ) {
        CompetitiveBrandReport report = getById(id);

        report.setBrandName(dto.getBrandName());
        report.setProductCategory(dto.getProductCategory());
        report.setHospitalName(dto.getHospitalName());
        report.setObservations(dto.getObservations());
        report.setManagerNotified(dto.isManagerNotified());

        if (dto.getFieldExecutiveId() != null)
            report.setFieldExecutive(getFieldExecutive(dto.getFieldExecutiveId()));

        if (dto.getProductId() != null)
            report.setProduct(getProduct(dto.getProductId()));

        if (dto.getDoctorId() != null)
            report.setDoctor(getDoctor(dto.getDoctorId()));

        if (image != null && !image.isEmpty()) {
            report.setImageUrl(fileStorageService.storeFile(image));
        }

        return reportRepository.save(report);
    }

    public CompetitiveBrandReport getById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Competitive report not found: " + id)
                );
    }

    public List<CompetitiveBrandReport> getAll() {
        return reportRepository.findAll();
    }

    public void delete(Long id) {
        reportRepository.delete(getById(id));
    }

    /* -------- Helpers -------- */

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
