package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.MonthlyTargetPlan;
import com.instantsolutions.larimarpharma.exceptions.BadRequestException;
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
            Long feId,
            Integer month,
            Integer year,
            Double primaryTarget,
            Double secondaryTarget
    ) {
        // ✅ Slot planning day check (example: only allowed on 25th)
        if (LocalDate.now().getDayOfMonth() != 2) {
            throw new BadRequestException("Target planning allowed only on slot planning day");
        }

        FieldExecutive fe = fieldExecutiveRepository.findById(feId)
                .orElseThrow(() -> new BadRequestException("Field Executive not found"));

        // ✅ Prevent duplicate plans
        targetPlanRepository.findByFieldExecutiveIdAndMonthAndYear(feId, month, year)
                .ifPresent(p -> {
                    throw new BadRequestException("Target plan already exists for this month");
                });

        MonthlyTargetPlan plan = MonthlyTargetPlan.builder()
                .fieldExecutive(fe)
                .month(month)
                .year(year)
                .primarySalesTarget(primaryTarget)
                .secondarySalesTarget(secondaryTarget)
                .build();

        return targetPlanRepository.save(plan);
    }

    /**
     * Admin fetches all target plans for selected month
     */
    @Transactional(readOnly = true)
    public List<MonthlyTargetPlan> getPlansForMonth(Integer month, Integer year) {
        return targetPlanRepository.findAllByMonthAndYear(month, year);
    }
}
