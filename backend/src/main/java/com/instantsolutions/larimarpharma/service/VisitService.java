package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.repository.DoctorRepository;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.ProductRepository;
import com.instantsolutions.larimarpharma.repository.VisitRepository;
import com.instantsolutions.larimarpharma.utils.DateUtil;
import com.instantsolutions.larimarpharma.utils.GeoUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final DoctorRepository doctorRepository;
    private final ProductRepository productRepository;

    public VisitDashboardResponse getDashboard(Long fieldExecutiveId) {

        LocalDate now = LocalDate.now();

        LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = now.withDayOfMonth(now.lengthOfMonth())
                .atTime(LocalTime.MAX);

        VisitCountProjection counts = visitRepository.getCompletedVisitCountsForMonth(
                fieldExecutiveId,
                startOfMonth,
                endOfMonth
        );

        long doctorCompleted = counts.getDoctor();
        long pharmacistCompleted = counts.getPharmacist();
        long stockistCompleted = counts.getStockist();

        long totalDoctorVisitsForMonth =
                visitRepository.countByFieldExecutiveIdAndVisitTypeAndScheduledDateBetween(
                        fieldExecutiveId,
                        Visit.VisitType.DOCTOR,
                        startOfMonth,
                        endOfMonth
                );

        String doctorTargetProgress = calculateProgress(
                doctorCompleted,
                totalDoctorVisitsForMonth
        );

        return VisitDashboardResponse.builder()
                .doctorVisits(doctorCompleted)
                .pharmacyVisits(pharmacistCompleted)
                .stockistVisit(stockistCompleted)
                .totalDoctorVisitsForTheCurrentMonth(totalDoctorVisitsForMonth)
                .doctorTargetProgress(doctorTargetProgress)
                .build();
    }

    private String calculateProgress(long completed, long target) {
        if (target == 0) return "0%";
        long percentage = Math.round((completed * 100.0) / target);
        return percentage + "%";
    }

    @Transactional
    public VisitResponseDto planVisitByWeek(VisitPlanByWeekDto dto) {

        LocalDate visitDate = DateUtil.calculateVisitDate(
                dto.getWeekNumber(),
                dto.getDayOfWeek()
        );

        // Prevent past date planning
        if (visitDate.isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot plan visit for past date");
        }

//        if (visitRepository.existsByFieldExecutiveIdAndDoctorIdAndVisitDate(
//                dto.getFieldExecutiveId(),
//                dto.getDoctorId(),
//                visitDate)) {
//            throw new IllegalStateException("Visit already planned for this date");
//        }

        FieldExecutive fe = fieldExecutiveRepository.findById(dto.getFieldExecutiveId())
                .orElseThrow(() -> new EntityNotFoundException("FE not found"));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        Visit visit = Visit.builder()
                .fieldExecutive(fe)
                .doctor(doctor)
                .visitType(dto.getVisitType())
                .visitDate(visitDate)
                .weekNumber(dto.getWeekNumber())
                .dayOfWeek(dto.getDayOfWeek())
                .status(Visit.VisitStatus.SCHEDULED)
                .scheduledDate(visitDate.atStartOfDay())
                .pharmacyName(dto.getPharmacyName())
                .contactPerson(dto.getContactPerson())
                .contactNumber(dto.getContactNumber())
                .stockistType(dto.getStockistType())
                .stockistName(dto.getStockistName())
                .build();
        Visit visit1 = visitRepository.save(visit);
        return mapToDto(visit1);
    }

    @Transactional
    public VisitResponseDto markVisit(MarkVisitRequestDto dto) {

        Visit visit = visitRepository.findById(dto.getVisitId())
                .orElseThrow(() -> new EntityNotFoundException("Visit not found"));

        Doctor doctor = visit.getDoctor();

        if (dto.getLatitude() == null || dto.getLongitude() == null) {
            throw new IllegalArgumentException("Latitude & Longitude required");
        }

        // If doctor has no location → save it
        if (doctor.getLatitude() == null || doctor.getLongitude() == null) {

            doctor.setLatitude(dto.getLatitude());
            doctor.setLongitude(dto.getLongitude());
            doctorRepository.save(doctor);

        } else {
            double distance = GeoUtil.distanceInMeters(
                    Double.parseDouble(doctor.getLatitude()),
                    Double.parseDouble(doctor.getLongitude()),
                    Double.parseDouble(dto.getLatitude()),
                    Double.parseDouble(dto.getLongitude())
            );

            if (distance > 100) {
                throw new IllegalStateException(
                        "You are not within 100 meters of the doctor location"
                );
            }
        }

        visit.setActualDate(LocalDateTime.now());
        visit.setActualVisitTime(LocalDateTime.now());

        //  Update visit fields
        visit.setStatus(dto.getStatus());
        visit.setNotes(dto.getNotes());
        visit.setActivitiesPerformed(dto.getActivitiesPerformed());

        if (dto.getConvertedProducts() != null && !dto.getConvertedProducts().isEmpty()) {

            List<ConvertedProduct> visitProducts = dto.getConvertedProducts().stream()
                    .map(p -> ConvertedProduct.builder()
                            .visit(visit)
                            .product(productRepository.getReferenceById(p.getProductId()))
                            .quantity(p.getQuantity())
                            .value(p.getValue())
                            .build())
                    .toList();

            visit.getConvertedProducts().clear();
            visit.getConvertedProducts().addAll(visitProducts);
        }


        Visit visit1 = visitRepository.save(visit);
        return mapToDto(visit1);
    }

    private VisitResponseDto mapToDto(Visit visit) {
        return VisitResponseDto.builder()
                .id(visit.getId())
                .visitDate(visit.getVisitDate())
                .stockistType(visit.getStockistType())
                .fieldExecutiveId(visit.getFieldExecutive().getId())
                .fieldExecutiveName(visit.getFieldExecutive().getName())
                .doctorId(
                        visit.getDoctor() != null ? visit.getDoctor().getId() : null
                )
                .pharmacyId(
                        visit.getPharmacy() != null ? visit.getPharmacy().getId() : null
                )
                .build();
    }

    public List<DoctorVisitSlotDto> getSlotVisits(
            Long fieldExecutiveId,
            Integer weekNumber,
            Integer dayOfWeek
    ) {

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<Visit> visits = visitRepository.findVisitsForSlot(
                fieldExecutiveId,
                weekNumber,
                dayOfWeek,
                startOfMonth,
                endOfMonth
        );

        Map<Long, Long> completedCountMap = visitRepository
                .countCompletedVisitsPerDoctor(fieldExecutiveId, startOfMonth, endOfMonth)
                .stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[1]
                ));

        Map<Long, Long> plannedCountMap = visitRepository
                .countPlannedVisitsPerDoctor(fieldExecutiveId, startOfMonth, endOfMonth)
                .stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[1]
                ));

        return visits.stream().map(v -> {

            Long doctorId = v.getDoctor().getId();

            return DoctorVisitSlotDto.builder()
                    .visitId(v.getId())
                    .doctorId(doctorId)
                    .doctorName(v.getDoctor().getName())
                    .specialization(v.getDoctor().getDesignation())
                    .hospitalName(v.getDoctor().getHospitalName())
                    .weekNumber(v.getWeekNumber())
                    .dayOfWeek(v.getDayOfWeek())
                    .practiceType(String.valueOf(v.getDoctor().getPracticeType()))
                    .category(String.valueOf(v.getDoctor().getCategory()))
                    .status(v.getStatus())
                    .visitType(v.getVisitType())
                    .completedVisitCount(completedCountMap.getOrDefault(doctorId, 0L))
                    .plannedVisitCount(plannedCountMap.getOrDefault(doctorId, 0L))
                    .build();
        }).toList();
    }

    public List<PharmacyVisitSlotDto> getPharmacySlotVisits(
            Long fieldExecutiveId,
            Integer weekNumber,
            Integer dayOfWeek
    ) {

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<Visit> visits = visitRepository.findPharmacyVisitsForSlot(
                fieldExecutiveId,
                weekNumber,
                dayOfWeek,
                startOfMonth,
                endOfMonth
        );

        Map<Long, Long> completedCountMap =
                visitRepository.countCompletedVisitsPerPharmacy(
                        fieldExecutiveId, startOfMonth, endOfMonth
                ).stream().collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[1]
                ));

        Map<Long, Long> plannedCountMap =
                visitRepository.countPlannedVisitsPerPharmacy(
                        fieldExecutiveId, startOfMonth, endOfMonth
                ).stream().collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[1]
                ));

        return visits.stream().map(v -> {

            Long pharmacyId = v.getPharmacy().getId();

            return PharmacyVisitSlotDto.builder()
                    .visitId(v.getId())
                    .pharmacyId(pharmacyId)
                    .pharmacyName(v.getPharmacy().getPharmacyName())
                    .weekNumber(v.getWeekNumber())
                    .dayOfWeek(v.getDayOfWeek())
                    .status(v.getStatus())
                    .visitType(v.getVisitType())
                    .completedVisitCount(
                            completedCountMap.getOrDefault(pharmacyId, 0L)
                    )
                    .plannedVisitCount(
                            plannedCountMap.getOrDefault(pharmacyId, 0L)
                    )
                    .build();
        }).toList();
    }

    public List<CompletedVisitDto> getCompletedVisits(Long fieldExecutiveId) {

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        return visitRepository
                .findAllCompletedVisits(fieldExecutiveId, startOfMonth, endOfMonth)
                .stream()
                .map(this::mapToCompletedVisitDto)
                .toList();
    }

    private CompletedVisitDto mapToCompletedVisitDto(Visit v) {

        CompletedVisitDto.CompletedVisitDtoBuilder builder =
                CompletedVisitDto.builder()
                        .visitId(v.getId())
                        .visitType(v.getVisitType())
                        .visitDate(v.getVisitDate())
                        .weekNumber(v.getWeekNumber())
                        .dayOfWeek(v.getDayOfWeek())
                        .actualVisitTime(v.getActualVisitTime())
                        .location(v.getLocation())
                        .notes(v.getNotes());

        switch (v.getVisitType()) {

            case DOCTOR -> builder.doctor(mapDoctor(v.getDoctor()));

            case PHARMACIST -> builder.pharmacy(mapPharmacy(v.getPharmacy()));

            case STOCKIST -> builder.stockist(mapStockist(v.getStockist()));
        }

        return builder.build();
    }

    private DoctorDetailsDto mapDoctor(Doctor d) {
        return DoctorDetailsDto.builder()
                .id(d.getId())
                .name(d.getName())
                .category(d.getCategory())
                .practiceType(d.getPracticeType())
                .designation(d.getDesignation())
                .hospitalName(d.getHospitalName())
                .location(d.getLocation())
                .contactNumber(d.getContactNumber())
                .doctorCode(d.getDoctorCode())
                .latitude(d.getLatitude())
                .longitude(d.getLongitude())
                .active(d.isActive())
                .build();
    }

    private PharmacyDetailsDto mapPharmacy(Pharmacy p) {
        return PharmacyDetailsDto.builder()
                .id(p.getId())
                .pharmacyName(p.getPharmacyName())
                .location(p.getLocation())
                .contactPerson(p.getContactPerson())
                .contactNumber(p.getContactNumber())
                .doctorId(
                        p.getDoctor() != null ? p.getDoctor().getId() : null
                )
                .doctorName(
                        p.getDoctor() != null ? p.getDoctor().getName() : null
                )
                .build();
    }

    private StockistDetailsDto mapStockist(Stockist s) {
        return StockistDetailsDto.builder()
                .id(s.getId())
                .name(s.getName())
                .type(s.getType())
                .contactPerson(s.getContactPerson())
                .contactNumber(s.getContactNumber())
                .location(s.getLocation())
                .active(s.isActive())
                .build();
    }

}
