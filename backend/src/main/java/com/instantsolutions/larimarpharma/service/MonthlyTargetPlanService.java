package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.MonthlyTargetPlanRequestDto;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.MonthlyTargetPlan;
import com.instantsolutions.larimarpharma.exceptions.BadRequestException;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.MonthlyTargetPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MonthlyTargetPlanService {

    private final MonthlyTargetPlanRepository targetPlanRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;

    /**
     * FE creates target plan
     */
    @Transactional
    public MonthlyTargetPlan createTargetPlan(
            MonthlyTargetPlanRequestDto dto
    ) {
        // ✅ Slot planning day check (example: only allowed on 25th)
        if (LocalDate.now().getDayOfMonth() != 2) {
            throw new BadRequestException("Target planning allowed only on slot planning day");
        }

        FieldExecutive fe = fieldExecutiveRepository.findById(dto.getFieldExecutiveId())
                .orElseThrow(() -> new BadRequestException("Field Executive not found"));

        // ✅ Prevent duplicate plans
        targetPlanRepository.findByFieldExecutiveIdAndMonthAndYear(dto.getFieldExecutiveId(), dto.getMonth(), dto.getYear())
                .ifPresent(p -> {
                    throw new BadRequestException("Target plan already exists for this month");
                });

        MonthlyTargetPlan plan = MonthlyTargetPlan.builder()
                .fieldExecutive(fe)
                .month(dto.getMonth())
                .year(dto.getYear())
                .primarySalesTarget(dto.getPrimaryTarget())
                .secondarySalesTarget(dto.getSecondaryTarget())
                .build();

        return targetPlanRepository.save(plan);
    }

    @Transactional(readOnly = true)
    public MonthlyTargetPlan getForMonth(
            Long feId, Integer month, Integer year) {

        return
                targetPlanRepository.findByFieldExecutiveIdAndMonthAndYear(feId, month, year)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Target plan not found"));


    }

    /**
     * Admin fetches all target plans for selected month
     */
    @Transactional(readOnly = true)
    public List<MonthlyTargetPlan> getPlansForMonth(Integer month, Integer year) {
        return targetPlanRepository.findAllByMonthAndYear(month, year);
    }

    public MonthlyTargetPlan update(Long id, MonthlyTargetPlanRequestDto dto) {

        MonthlyTargetPlan plan = targetPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Target plan not found"));

        plan.setPrimarySalesTarget(dto.getPrimaryTarget());
        plan.setSecondarySalesTarget(dto.getSecondaryTarget());

        return targetPlanRepository.save(plan);
    }

    public List<MonthlyTargetPlan> getByFieldExecutive(Long feId) {
        return targetPlanRepository.findAllByFieldExecutiveId(feId);

    }
}
