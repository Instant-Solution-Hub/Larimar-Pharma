package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.DoctorConversion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DoctorConversionRepository extends JpaRepository<DoctorConversion, Long> {

    List<DoctorConversion> findByFieldExecutiveIdAndCreatedAtBetween(
            Long fieldExecutiveId,
            LocalDateTime start,
            LocalDateTime end
    );

    Optional<DoctorConversion> findByIdAndFieldExecutiveId(Long id, Long fieldExecutiveId);

    List<DoctorConversion> findByCreatedAtBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}

