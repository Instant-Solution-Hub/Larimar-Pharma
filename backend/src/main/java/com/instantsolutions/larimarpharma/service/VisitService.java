package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.controller.MarkStockistVisitRequestDto;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.repository.*;
import com.instantsolutions.larimarpharma.utils.DateUtil;
import com.instantsolutions.larimarpharma.utils.GeoUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.IsoFields;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.instantsolutions.larimarpharma.utils.DateUtil.*;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final DoctorRepository doctorRepository;
    private final PharmacyRepository pharmacyRepository;
    private final ProductRepository productRepository;

    private final StockistRepository stockistRepository;

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

        long completed = counts.getDoctor() + counts.getPharmacist();
        long pharmacistCompleted = counts.getPharmacist();
        long stockistCompleted = counts.getStockist();

        long totalDoctorVisitsForMonth =
                visitRepository.countByFieldExecutiveIdAndVisitTypeAndScheduledDateBetween(
                        fieldExecutiveId,
                        Visit.VisitType.DOCTOR,
                        startOfMonth,
                        endOfMonth
                );

        long totalPharmacistVisitsForMonth =
                visitRepository.countByFieldExecutiveIdAndVisitTypeAndScheduledDateBetween(
                        fieldExecutiveId,
                        Visit.VisitType.PHARMACIST,
                        startOfMonth,
                        endOfMonth
                );

        long totalTargetVisitsForMonth =
                totalDoctorVisitsForMonth + totalPharmacistVisitsForMonth;

        String doctorTargetProgress = calculateProgress(
                completed,
                totalTargetVisitsForMonth
        );

        return VisitDashboardResponse.builder()
                .doctorVisits(completed)
                .pharmacyVisits(pharmacistCompleted)
                .stockistVisit(stockistCompleted)
                .totalDoctorVisitsForTheCurrentMonth(totalTargetVisitsForMonth)
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
//        if (visitDate.isBefore(LocalDate.now())) {
//            throw new IllegalStateException("Cannot plan visit for past date");
//        }

//        if (visitRepository.existsByFieldExecutiveIdAndDoctorIdAndVisitDate(
//                dto.getFieldExecutiveId(),
//                dto.getDoctorId(),
//                visitDate)) {
//            throw new IllegalStateException("Visit already planned for this date");
//        }

        FieldExecutive fe = fieldExecutiveRepository.findById(dto.getFieldExecutiveId())
                .orElseThrow(() -> new EntityNotFoundException("FE not found"));

        if(dto.getVisitType().equals(Visit.VisitType.DOCTOR)){
            Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

            boolean alreadyPlanned = visitRepository
                    .existsByDoctorIdAndVisitDateAndVisitType(
                            dto.getDoctorId(),
                            visitDate,
                            Visit.VisitType.DOCTOR
                    );

            if (alreadyPlanned) {
                throw new IllegalStateException(
                        "Visit already planned for this doctor on the selected date"
                );
            }

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

        if(dto.getVisitType().equals(Visit.VisitType.PHARMACIST)){
            Pharmacy pharmacy = pharmacyRepository.findById(dto.getPharmacistId())
                    .orElseThrow(() -> new EntityNotFoundException("Pharmacy not found"));
            boolean alreadyPlanned = visitRepository
                    .existsByPharmacyIdAndVisitDateAndVisitType(
                            dto.getPharmacistId(),
                            visitDate,
                            Visit.VisitType.PHARMACIST
                    );

            if (alreadyPlanned) {
                throw new IllegalStateException(
                        "Visit already planned for this pharmacy on the selected date"
                );
            }

            Visit visit = Visit.builder()
                    .fieldExecutive(fe)
                    .pharmacy(pharmacy)
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

        return null;

    }

    @Transactional
    public VisitResponseDto markVisit(MarkVisitRequestDto dto) {
        Visit visit = visitRepository.findById(dto.getVisitId())
                .orElseThrow(() -> new EntityNotFoundException("Visit not found"));

        Doctor doctor = visit.getDoctor();
        Pharmacy pharmacy = visit.getPharmacy();


        if(visit.getVisitType().equals(Visit.VisitType.DOCTOR) &&
                !dto.getStatus().equals(Visit.VisitStatus.MISSED)){
            if (dto.getLatitude() == null || dto.getLongitude() == null) {
                throw new IllegalArgumentException("Please allow location access to mark the visit");
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
                            "You are not within 100 meters of the doctor/pharmacy location"
                    );
                }
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

    @Transactional
    public VisitResponseDto reMarkVisit(MarkVisitRequestDto dto) {

        Visit visit = visitRepository.findById(dto.getVisitId())
                .orElseThrow(() -> new EntityNotFoundException("Visit not found"));

        // 1️⃣ Only MISSED visits can be re-marked
        if (visit.getStatus() != Visit.VisitStatus.MISSED) {
            throw new IllegalStateException(
                    "Only missed visits can be re-marked"
            );
        }

//        // 2️⃣ Re-marking allowed only to COMPLETED
//        if (dto.getStatus() != Visit.VisitStatus.COMPLETED) {
//            throw new IllegalStateException(
//                    "Re-marked visit must be completed"
//            );
//        }

        // 3️⃣ Doctor-only restriction
        if (visit.getVisitType() == Visit.VisitType.DOCTOR) {

            Doctor doctor = visit.getDoctor();
            LocalDate visitDate = visit.getVisitDate();
            LocalDate today = LocalDate.now();

            boolean alreadyHasVisit =
                    visitRepository.existsByDoctorIdAndVisitDate(
                            doctor.getId(),
                            today
                    );

            if (alreadyHasVisit) {
                throw new IllegalStateException(
                        "Doctor already has a visit for this day"
                );
            }

            if(!dto.getStatus().equals(Visit.VisitStatus.MISSED)){
                // 📍 Location validation (same as markVisit)
                if (dto.getLatitude() == null || dto.getLongitude() == null) {
                    throw new IllegalArgumentException(
                            "Please allow location access"
                    );
                }

                if (doctor.getLatitude() != null && doctor.getLongitude() != null) {
                    double distance = GeoUtil.distanceInMeters(
                            Double.parseDouble(doctor.getLatitude()),
                            Double.parseDouble(doctor.getLongitude()),
                            Double.parseDouble(dto.getLatitude()),
                            Double.parseDouble(dto.getLongitude())
                    );

                    if (distance > 200) {
                        throw new IllegalStateException(
                                "You are not within 100 meters of the doctor location"
                        );
                    }
                }
            }


        }

        // Update visit (same fields as markVisit)
        visit.setStatus(Visit.VisitStatus.COMPLETED);
        visit.setActualDate(LocalDateTime.now());
        visit.setActualVisitTime(LocalDateTime.now());
        visit.setNotes(dto.getNotes());
        visit.setActivitiesPerformed(dto.getActivitiesPerformed());

        if (dto.getConvertedProducts() != null && !dto.getConvertedProducts().isEmpty()) {

            List<ConvertedProduct> visitProducts =
                    dto.getConvertedProducts().stream()
                            .map(p -> ConvertedProduct.builder()
                                    .visit(visit)
                                    .product(
                                            productRepository.getReferenceById(
                                                    p.getProductId()
                                            )
                                    )
                                    .quantity(p.getQuantity())
                                    .value(p.getValue())
                                    .build()
                            )
                            .toList();

            visit.getConvertedProducts().clear();
            visit.getConvertedProducts().addAll(visitProducts);
        }

        Visit savedVisit = visitRepository.save(visit);
        return mapToDto(savedVisit);
    }


    public VisitResponseDto markStockistVisit(MarkStockistVisitRequestDto dto) {

        FieldExecutive fe = fieldExecutiveRepository.findById(dto.getFieldExecutiveId())
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        Stockist stockist = stockistRepository.findById(dto.getStockistId())
                .orElseThrow(() -> new RuntimeException("Stockist not found"));

        LocalDateTime now = LocalDateTime.now();

        Visit visit = new Visit();
        visit.setVisitType(Visit.VisitType.STOCKIST);
        visit.setVisitDate(now.toLocalDate());
        visit.setActualDate(now);
        visit.setActualVisitTime(now);
        visit.setDayOfWeek(dto.getDayOfWeek());
        visit.setWeekNumber(dto.getWeekNumber());

        visit.setFieldExecutive(fe);
        visit.setStockist(stockist);
        visit.setStockistType(dto.getStockistType());

        visit.setStatus(
                dto.getStatus() != null ? dto.getStatus() : Visit.VisitStatus.COMPLETED
        );

        visit.setNotes(dto.getNotes());
        visit.setActivitiesPerformed(dto.getActivitiesPerformed());
        visit.setOrderValue(dto.getOrderValue());
        visit.setLocation(dto.getLocation());

        Visit savedVisit = visitRepository.save(visit);
        return mapToDto(savedVisit);
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

        LocalDate choosedDate = calculateVisitDate(weekNumber, dayOfWeek);
        System.out.println("CHOOSED DATE");
        System.out.println(choosedDate);

        LocalDate startOfMonth = getStartOfTheMonth();
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<Visit> visits = visitRepository.findVisitsForSlot(
                fieldExecutiveId,
//                weekNumber,
//                dayOfWeek,
//                choosedDate,
                choosedDate
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
                    .visitDate(v.getVisitDate())
                    .practiceType(String.valueOf(v.getDoctor().getPracticeType()))
                    .category(String.valueOf(v.getDoctor().getCategory()))
                    .status(v.getStatus())
                    .visitType(v.getVisitType())
                    .completedVisitCount(completedCountMap.getOrDefault(doctorId, 0L))
                    .plannedVisitCount(plannedCountMap.getOrDefault(doctorId, 0L))
                    .build();
        }).toList();
    }

    public List<DoctorVisitSlotDto> getCurrentMonthSlotVisits(
            Long fieldExecutiveId,
            Integer weekNumber,
            Integer dayOfWeek
    ) {

        LocalDate choosedDate = calculateVisitDateCurrentMonth(weekNumber, dayOfWeek);
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<Visit> visits = visitRepository.findVisitsForSlot(
                fieldExecutiveId,
                choosedDate
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

        LocalDate choosenDate = calculateVisitDate(weekNumber, dayOfWeek);

        LocalDate startOfMonth = getStartOfTheMonth();
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<Visit> visits = visitRepository.findPharmacyVisitsForSlot(
                fieldExecutiveId,
                choosenDate
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
                    .contactPerson(v.getPharmacy().getContactPerson())
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

    public List<PharmacyVisitSlotDto> getCurrentMonthPharmacySlotVisits(
            Long fieldExecutiveId,
            Integer weekNumber,
            Integer dayOfWeek
    ) {

        LocalDate choosedDate = calculateVisitDateCurrentMonth(weekNumber, dayOfWeek);
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<Visit> visits = visitRepository.findPharmacyVisitsForSlot(
                fieldExecutiveId,
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
                    .contactPerson(v.getPharmacy().getContactPerson())
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

    @Transactional
    public List<CompletedVisitDto> getCompletedVisits(Long fieldExecutiveId) {

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        return visitRepository
                .findAllCompletedVisits(fieldExecutiveId, startOfMonth, endOfMonth)
                .stream()
                .map(this::mapToCompletedVisitDto)
                .toList();
    }


    @Transactional
    public List<CompletedVisitDto> getMissedVisits(Long fieldExecutiveId) {

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        return visitRepository
                .findAllMissedVisits(fieldExecutiveId, startOfMonth, endOfMonth)
                .stream()
                .map(this::mapToCompletedVisitDto)
                .toList();
    }

    @Transactional
    public List<CompletedVisitDto> getAllMissedVisits() {

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        return visitRepository
                .findAllMissedVisits(startOfMonth, endOfMonth)
                .stream()
                .map(this::mapToCompletedVisitDto)
                .toList();
    }

    private CompletedVisitDto mapToCompletedVisitDto(Visit v) {

        CompletedVisitDto.CompletedVisitDtoBuilder builder =
                CompletedVisitDto.builder()
                        .visitId(v.getId())
                        .feEmpCode(v.getFieldExecutive().getEmployeeCode())
                        .feName(v.getFieldExecutive().getName())
                        .userRole("FIELD_EXECUTIVE")
                        .status(v.getStatus())
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

    /* ===== Doctors ===== */
    public List<ScheduledDoctorVisitDto> getTodayScheduledDoctors(Long fieldExecutiveId) {

        LocalDate today = LocalDate.now();

        return visitRepository
                .findTodayScheduledDoctorVisits(fieldExecutiveId, today)
                .stream()
                .map(v -> ScheduledDoctorVisitDto.builder()
                        .visitId(v.getId())
                        .doctorId(v.getDoctor().getId())
                        .doctorName(v.getDoctor().getName())
                        .category(v.getDoctor().getCategory())
                        .practiceType(v.getDoctor().getPracticeType())
                        .hospitalName(v.getDoctor().getHospitalName())
                        .location(v.getDoctor().getLocation())
                        .contactNumber(v.getDoctor().getContactNumber())
                        .status(v.getStatus())
                        .weekNumber(v.getWeekNumber())
                        .dayOfWeek(v.getDayOfWeek())
                        .build()
                )
                .toList();
    }

    /* ===== Pharmacies ===== */
    public List<ScheduledPharmacyVisitDto> getTodayScheduledPharmacies(Long fieldExecutiveId) {

        LocalDate today = LocalDate.now();

        return visitRepository
                .findTodayScheduledPharmacyVisits(fieldExecutiveId, today)
                .stream()
                .map(v -> ScheduledPharmacyVisitDto.builder()
                        .visitId(v.getId())
                        .pharmacyId(v.getPharmacy().getId())
                        .pharmacyName(v.getPharmacy().getPharmacyName())
                        .location(v.getPharmacy().getLocation())
                        .contactPerson(v.getPharmacy().getContactPerson())
                        .contactNumber(v.getPharmacy().getContactNumber())
                        .status(v.getStatus())
                        .weekNumber(v.getWeekNumber())
                        .dayOfWeek(v.getDayOfWeek())
                        .build()
                )
                .toList();
    }

    public List<TodayScheduledVisitDto> getTodayScheduledVisits(Long feId) {

//        ZoneId zone = ZoneId.of("Asia/Kolkata");
        LocalDate today = LocalDate.now();

        List<Visit> visits = visitRepository.findTodayScheduledVisitsByFieldExecutive(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay(),
                Visit.VisitStatus.SCHEDULED,
                feId
        );

        return visits.stream()
                .map(this::toTodayScheduledVisitDTO)
                .toList();
    }

    public List<TodayScheduledVisitDto> getTodaysVisits(Long feId) {
        LocalDate today = LocalDate.now();
        List<Visit> visits = visitRepository.findTodaysVisitsByFieldExecutive(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay(),
                feId
        );

        return visits.stream()
                .map(this::toTodayScheduledVisitDTO)
                .toList();
    }

    public List<TodayScheduledVisitDto> getTodaysAndMissedVisits(Long feId) {
        LocalDate today = LocalDate.now();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        List<Visit> visits = visitRepository.findTodaysAndMissedVisitsByFieldExecutive(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay(),
                monthStart,
                feId
        );

        return visits.stream()
                .map(this::toTodayScheduledVisitDTO)
                .toList();
    }


    private TodayScheduledVisitDto toTodayScheduledVisitDTO(Visit v) {

        Doctor d = v.getDoctor();
        Pharmacy p = v.getPharmacy();
        FieldExecutive fe = v.getFieldExecutive();

        return new TodayScheduledVisitDto(
                v.getId(),
                v.getVisitType(),
                v.getVisitDate(),
               String.valueOf( v.getStatus()),

                d != null ? d.getId() : null,
                d != null ? d.getName() : null,
                d != null ? d.getDesignation() : null,
                d != null ? String.valueOf(d.getCategory()) : null,
                d != null ? String.valueOf(d.getPracticeType()) : null,
                d != null ? d.getHospitalName() : "",

                p != null ? p.getId() : null,
                p != null ? p.getPharmacyName() : null,
                p != null ? p.getContactPerson() : null,
                p != null ? p.getContactNumber() : null,

                fe.getId(),
                fe.getName()
        );
    }

    @Transactional(readOnly = true)
    public VisitComplianceResponse getVisitCompliance(Long fieldExecutiveId, String weekFilter) {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();
        LocalDateTime endOfMonth = lastDayOfMonth.atTime(LocalTime.MAX);

        Integer weekNumber = null;
        if (!"all".equals(weekFilter) && weekFilter.startsWith("week")) {
            weekNumber = Integer.parseInt(weekFilter.replace("week", ""));
        }

        // Get filtered records
        List<Visit> visits = visitRepository.findComplianceRecords(
                fieldExecutiveId,
                weekNumber,
                startOfMonth,
                endOfMonth
        );

        // Convert to DTOs
        List<ComplianceRecordDto> records = visits.stream()
                .map(this::convertToComplianceRecordDto)
                .collect(Collectors.toList());

        // Calculate stats
        ComplianceStatsDto stats = calculateComplianceStats(visits, weekNumber, fieldExecutiveId);

        // Calculate total weeks in month
        int totalWeeks = calculateWeeksInMonth(now);

        return VisitComplianceResponse.builder()
                .stats(stats)
                .records(records)
                .totalWeeks(totalWeeks)
                .build();
    }

    private ComplianceRecordDto convertToComplianceRecordDto(Visit visit) {
        String name = "";
        String category = "";

        if (visit.getVisitType() == Visit.VisitType.DOCTOR && visit.getDoctor() != null) {
            name = visit.getDoctor().getName();
            category = visit.getDoctor().getCategory() != null
                    ? visit.getDoctor().getCategory().name()
                    : "N/A";
        } else if (visit.getVisitType() == Visit.VisitType.PHARMACIST && visit.getPharmacy() != null) {
            name = visit.getPharmacy().getPharmacyName();
            category = "Pharmacist";
        }

        return ComplianceRecordDto.builder()
                .id(String.valueOf(visit.getId()))
                .name(name)
                .category(category)
                .scheduledDate(visit.getScheduledDate() != null
                        ? visit.getScheduledDate().toLocalDate()
                        : visit.getVisitDate())
                .status(visit.getStatus().name().toLowerCase())
                .week(visit.getWeekNumber())
                .visitType(visit.getVisitType().name().toLowerCase())
                .reason(getMissedReason(visit))
                .build();
    }

    private String getMissedReason(Visit visit) {
        if (visit.getStatus() == Visit.VisitStatus.MISSED) {
            if (visit.getNotes() != null && !visit.getNotes().isEmpty()) {
                return visit.getNotes();
            }
            return "Not specified";
        }
        return null;
    }

    private ComplianceStatsDto calculateComplianceStats(List<Visit> visits, Integer weekNumber, Long fieldExecutiveId) {
        int scheduled = visits.size();
        int completed = (int) visits.stream()
                .filter(v -> v.getStatus() == Visit.VisitStatus.COMPLETED)
                .count();
        int missed = (int) visits.stream()
                .filter(v -> v.getStatus() == Visit.VisitStatus.MISSED)
                .count();

        int doctorVisits = (int) visits.stream()
                .filter(v -> v.getVisitType() == Visit.VisitType.DOCTOR)
                .count();
        int doctorCompleted = (int) visits.stream()
                .filter(v -> v.getVisitType() == Visit.VisitType.DOCTOR
                        && v.getStatus() == Visit.VisitStatus.COMPLETED)
                .count();

        int pharmacistVisits = (int) visits.stream()
                .filter(v -> v.getVisitType() == Visit.VisitType.PHARMACIST)
                .count();
        int pharmacistCompleted = (int) visits.stream()
                .filter(v -> v.getVisitType() == Visit.VisitType.PHARMACIST
                        && v.getStatus() == Visit.VisitStatus.COMPLETED)
                .count();

        int complianceRate = scheduled > 0 ? Math.round((completed * 100) / scheduled) : 0;

        return ComplianceStatsDto.builder()
                .scheduled(scheduled)
                .completed(completed)
                .missed(missed)
                .complianceRate(complianceRate)
                .doctorVisits(doctorVisits)
                .doctorCompleted(doctorCompleted)
                .pharmacistVisits(pharmacistVisits)
                .pharmacistCompleted(pharmacistCompleted)
                .build();
    }

    private int calculateWeeksInMonth(LocalDate date) {
        LocalDate firstDay = date.withDayOfMonth(1);
        LocalDate lastDay = date.withDayOfMonth(date.lengthOfMonth());

        // Calculate week numbers (assuming ISO week definition)
        int firstWeek = firstDay.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int lastWeek = lastDay.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        if (firstWeek > lastWeek && lastDay.getYear() > firstDay.getYear()) {
            // Cross-year case
            lastWeek += 52;
        }

        return lastWeek - firstWeek + 1;
    }


    @Transactional(readOnly = true)
    public MonthlyDoctorTargetProgressDto getMonthlyDoctorTargetProgress(Long feId) {

        LocalDate now = LocalDate.now();
        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = now.withDayOfMonth(now.lengthOfMonth()).atTime(LocalTime.MAX);

        // Fixed targets
        Map<String, Integer> targetDoctors = Map.of(
                "A_PLUS", 30,
                "A", 60,
                "B", 10
        );

        Map<String, Integer> visitsPerDoctor = Map.of(
                "A_PLUS", 3,
                "A", 2,
                "B", 1
        );

        // Fetch DB data
        Map<String, Integer> completedVisitsMap =
                visitRepository.countCompletedVisitsByCategory(feId, start, end)
                        .stream()
                        .collect(Collectors.toMap(
                                r -> r[0].toString(),
                                r -> ((Long) r[1]).intValue()
                        ));

        Map<String, Integer> completedDoctorsMap =
                visitRepository.countDistinctDoctorsVisitedByCategory(feId, start, end)
                        .stream()
                        .collect(Collectors.toMap(
                                r -> r[0].toString(),
                                r -> ((Long) r[1]).intValue()
                        ));

        List<CategoryProgressDto> categories =
                targetDoctors.keySet().stream().map(cat -> {

                    int target = targetDoctors.get(cat);
                    int visitsEach = visitsPerDoctor.get(cat);

                    return CategoryProgressDto.builder()
                            .category(cat)
                            .label(cat.equals("A_PLUS") ? "A+" : cat)
                            .targetDoctors(target)
                            .visitsPerDoctor(visitsEach)
                            .completedDoctors(completedDoctorsMap.getOrDefault(cat, 0))
                            .completedVisits(completedVisitsMap.getOrDefault(cat, 0))
                            .build();
                }).toList();

        int totalTargetVisits = categories.stream()
                .mapToInt(c -> c.getTargetDoctors() * c.getVisitsPerDoctor())
                .sum();

        int totalCompletedVisits = categories.stream()
                .mapToInt(CategoryProgressDto::getCompletedVisits)
                .sum();

        int overallProgress =
                totalTargetVisits == 0 ? 0 :
                        Math.round((totalCompletedVisits * 100f) / totalTargetVisits);

        return MonthlyDoctorTargetProgressDto.builder()
                .totalTargetVisits(totalTargetVisits)
                .totalCompletedVisits(totalCompletedVisits)
                .overallProgress(overallProgress)
                .categories(categories)
                .build();
    }



    public void deleteVisit(Long id) {
      visitRepository.deleteById(id);
    }


    public List<TodayScheduledVisitDto> getScheduledVisitsForManager(
            Long managerId,
            Integer weekNumber,
            Integer dayOfWeek
    ) {
        List<Visit> visits = visitRepository.findScheduledDoctorVisitsForManagerByWeekAndDay(
                managerId,
                weekNumber,
                dayOfWeek
        );
        return visits.stream().map(this::toTodayScheduledVisitDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<TodayScheduledVisitDto> getVisitsForWeekAndDay(
            Long fieldExecutiveId,
            Integer weekNumber,
            Integer dayOfWeek
    ) {
        LocalDate now = LocalDate.now();

        LocalDateTime startOfMonth =
                now.withDayOfMonth(1).atStartOfDay();

        LocalDateTime endOfMonth =
                now.withDayOfMonth(now.lengthOfMonth())
                        .atTime(LocalTime.MAX);

        List<Visit> visits =
                visitRepository.findScheduledVisitsForFieldExecutiveByWeekAndDay(
                        fieldExecutiveId,
                        weekNumber,
                        dayOfWeek,
                        startOfMonth,
                        endOfMonth
                );

        return visits.stream()
                .map(this::toTodayScheduledVisitDTO)
                .toList();
    }


    // Services for admin
    @Transactional
    public CompletedVisitDto markVisitAsCompleted(Long visitId) {

        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Visit not found with id: " + visitId));

        // Prevent re-completing
        if (visit.getStatus() == Visit.VisitStatus.COMPLETED) {
            throw new IllegalStateException("Visit is already completed");
        }

        visit.setStatus(Visit.VisitStatus.COMPLETED);
        visit.setNotes("Visit marked as completed by admin");
//        visit.setActualVisitTime(LocalDateTime.now());
//        visit.setActualDate(LocalDateTime.now());
        Visit updatedVisit = visitRepository.save(visit);
        return mapToCompletedVisitDto(updatedVisit);
    }

    @Transactional(readOnly = true)
    public DoctorVisitProgressDto getDoctorVisitTracking(
            Long feId,
            Long doctorId
    ) {
        LocalDate startDate = getStartOfTheMonth();
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));


        long plannedVisits = visitRepository.countDoctorVisitsForDateRange(
                feId,
                doctorId,
                startDate.atStartOfDay(),
                endDate.atStartOfDay()
        );

        int requiredVisits = switch (doctor.getCategory()) {
            case A_PLUS -> 3;
            case A -> 2;
            case B -> 1;
        };

        return DoctorVisitProgressDto.builder()
                .doctorId(doctor.getId())
                .doctorName(doctor.getName())
                .category(doctor.getCategory().name())
                .requiredVisits(requiredVisits)
                .plannedVisits((int) plannedVisits)
                .progress(plannedVisits + "/" + requiredVisits)
                .build();
    }

    @Transactional(readOnly = true)
    public DoctorVisitProgressDto getDoctorVisitCompletionTracking(
            Long feId,
            Long doctorId
    ) {
//        ZoneId zone = ZoneId.of("Asia/Kolkata");
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));


        long plannedVisits = visitRepository.countDoctorCompletedVisitsForDateRange(
                feId,
                doctorId,
                startDate.atStartOfDay(),
                endDate.atStartOfDay()
        );

        int requiredVisits = switch (doctor.getCategory()) {
            case A_PLUS -> 3;
            case A -> 2;
            case B -> 1;
        };

        return DoctorVisitProgressDto.builder()
                .doctorId(doctor.getId())
                .doctorName(doctor.getName())
                .category(doctor.getCategory().name())
                .requiredVisits(requiredVisits)
                .plannedVisits((int) plannedVisits)
                .progress(plannedVisits + "/" + requiredVisits)
                .build();
    }

    public VisitSummaryResponseDto getVisitSummary(
            Long fieldExecutiveId,
            LocalDate from,
            LocalDate to
    ) {
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(23, 59, 59);

        return visitRepository.getFullVisitSummary(fieldExecutiveId, fromDateTime, toDateTime)
                .map(projection -> new VisitSummaryResponseDto(
                        projection.getCompletedVisitCount(),
                        projection.getMissedVisitCount(),
                        projection.getCompletedDoctorVisitCount(),
                        projection.getCompletedPharmacistVisitCount(),
                        projection.getMissedDoctorVisitCount(),
                        projection.getMissedPharmacistVisitCount(),
                        projection.getCompletedAPlusVisits(),
                        projection.getMissedAPlusVisits(),
                        projection.getCompletedAVisits(),
                        projection.getMissedAVisits(),
                        projection.getCompletedBVisits(),
                        projection.getMissedBVisits()
                ))
                .orElseGet(() -> new VisitSummaryResponseDto(0, 0,
                        0, 0,
                        0, 0,
                        0, 0,
                        0, 0,
                        0, 0));
    }




}
