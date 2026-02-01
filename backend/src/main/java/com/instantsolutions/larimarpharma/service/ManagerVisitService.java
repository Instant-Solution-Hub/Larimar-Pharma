package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.AssignManagerVisitRequest;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.entity.ManagerVisit;
import com.instantsolutions.larimarpharma.entity.Visit;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.ManagerRepository;
import com.instantsolutions.larimarpharma.repository.ManagerVisitRepository;
import com.instantsolutions.larimarpharma.repository.VisitRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ManagerVisitService {

    private final ManagerRepository managerRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final VisitRepository visitRepository;
    private final ManagerVisitRepository managerVisitRepository;

    @Transactional
    public void assignManagerToVisit(AssignManagerVisitRequest request) {

        // Rule: Manager can't select multiple FEs for same day
        if (managerVisitRepository.existsByManagerIdAndWeekNumberAndDayOfWeek(
                request.getManagerId(),
                request.getWeekNumber(),
                request.getDayOfWeek()
        )) {
            throw new IllegalStateException(
                    "Manager already assigned for this week and day"
            );
        }

        List<Visit> visits = visitRepository.findEligibleManagerVisits(
                request.getFieldExecutiveId(),
                request.getWeekNumber(),
                request.getDayOfWeek()
        );

        if (visits.isEmpty()) {
            throw new IllegalStateException("No A+ or A visits found");
        }

        Manager manager = managerRepository.findById(request.getManagerId())
                .orElseThrow(() -> new EntityNotFoundException("Manager not found"));

        FieldExecutive fe = fieldExecutiveRepository.findById(request.getFieldExecutiveId())
                .orElseThrow(() -> new EntityNotFoundException("FE not found"));

        for (Visit visit : visits) {

            // Safety: one manager visit per visit
            if (visit.getManagerVisit() != null) {
                continue;
            }

            ManagerVisit mv = ManagerVisit.builder()
                    .manager(manager)
                    .fieldExecutive(fe)
                    .originalVisit(visit)

                    // snapshot
                    .visitDate(visit.getVisitDate())
                    .weekNumber(visit.getWeekNumber())
                    .dayOfWeek(visit.getDayOfWeek())
                    .visitType(visit.getVisitType())
                    .status(visit.getStatus())
                    .scheduledDate(visit.getScheduledDate())

                    .doctorId(visit.getDoctor().getId())
                    .doctorName(visit.getDoctor().getName())
                    .doctorCategory(visit.getDoctor().getCategory())
                    .hospitalName(visit.getDoctor().getHospitalName())

                    .build();

            managerVisitRepository.save(mv);
            visit.setManagerVisit(mv);
        }
    }


}
