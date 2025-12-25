package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.MonthlyTargetPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MonthlyTargetPlanRepository extends JpaRepository<MonthlyTargetPlan, Long> {

    Optional<MonthlyTargetPlan> findByFieldExecutiveIdAndMonthAndYear(
            Long feId, Integer month, Integer year
    );

    List<MonthlyTargetPlan> findAllByMonthAndYear(Integer month, Integer year);
}
