package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.DoctorChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorChangeRequestRepository
        extends JpaRepository<DoctorChangeRequest, Long> {

    List<DoctorChangeRequest> findByFieldExecutiveId(Long feId);

    List<DoctorChangeRequest> findByStatus(
            DoctorChangeRequest.RequestStatus status
    );

    List<DoctorChangeRequest> findByFieldExecutiveIdAndStatus(
            Long feId,
            DoctorChangeRequest.RequestStatus status
    );

}