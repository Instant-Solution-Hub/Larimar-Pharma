package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query("""
        SELECT p
        FROM Promotion p
        WHERE p.active = true
          AND p.endDate >= :now
        ORDER BY p.startDate ASC
    """)
    List<Promotion> findActiveAndUpcoming(@Param("now") LocalDateTime now);

    // Total promotions (all records)
    long count();

    // Active promotions
    @Query("""
        SELECT COUNT(p)
        FROM Promotion p
        WHERE p.active = true
          AND p.startDate <= :now
          AND p.endDate >= :now
    """)
    long countActivePromotions(@Param("now") LocalDateTime now);

    // Upcoming promotions
    @Query("""
        SELECT COUNT(p)
        FROM Promotion p
        WHERE p.active = true
          AND p.startDate > :now
    """)
    long countUpcomingPromotions(@Param("now") LocalDateTime now);
}
