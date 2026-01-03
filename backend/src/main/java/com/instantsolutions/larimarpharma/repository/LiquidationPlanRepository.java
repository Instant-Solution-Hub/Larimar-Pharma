package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.Doctor;
import com.instantsolutions.larimarpharma.entity.LiquidationPlan;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LiquidationPlanRepository extends JpaRepository<LiquidationPlan, Long> {

    List<LiquidationPlan> findByFieldExecutive(FieldExecutive fieldExecutive);

    List<LiquidationPlan> findByFieldExecutiveId(Long fieldExecutiveId);

    List<LiquidationPlan> findByProductId(Long productId);

    // ✅ NEW: FE + Product filter
    List<LiquidationPlan> findByFieldExecutiveIdAndProductId(
            Long fieldExecutiveId,
            Long productId
    );

    List<LiquidationPlan> findByFieldExecutiveIdAndProductIdAndCreatedAtBetween(
            Long feId,
            Long productId,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
        SELECT COALESCE(SUM(lp.targetLiquidation), 0)
        FROM LiquidationPlan lp
        WHERE lp.fieldExecutive.id = :feId
          AND lp.product.id = :productId
          AND lp.createdAt BETWEEN :start AND :end
    """)
    Integer getUsedUnitsForMonth(
            Long feId,
            Long productId,
            LocalDateTime start,
            LocalDateTime end
    );

     Optional<LiquidationPlan> findByFieldExecutiveAndProductAndDoctorAndStatus(
            FieldExecutive fieldExecutive,
            Product product,
            Doctor doctor,
            LiquidationPlan.PlanStatus status
    ) ;

}

