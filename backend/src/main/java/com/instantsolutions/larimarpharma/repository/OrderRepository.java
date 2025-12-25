package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.Order;
import com.instantsolutions.larimarpharma.entity.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndFieldExecutiveId(Long orderId, Long feId);

    List<Order> findAllByFieldExecutiveId(Long feId);

    boolean existsByIdAndFieldExecutiveId(Long orderId, Long feId);

    List<Order> findAllByFieldExecutiveIdAndStatusNot(
            Long feId, OrderStatus status
    );
    long countByFieldExecutiveIdAndOrderDateBetweenAndStatusNot(
            Long feId,
            LocalDateTime start,
            LocalDateTime end,
            OrderStatus excludedStatus
    );

    long countByFieldExecutiveIdAndOrderDateBetweenAndStatus(
            Long feId,
            LocalDateTime start,
            LocalDateTime end,
            OrderStatus status
    );


    // 🔹 ALL orders for current month (including CANCELLED), DESC order
    List<Order> findAllByFieldExecutiveIdAndOrderDateBetweenOrderByOrderDateDesc(
            Long feId,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.fieldExecutive.id = :feId
          AND o.status = :status
          AND o.orderDate BETWEEN :start AND :end
    """)
    Double sumTotalAmountByStatusForMonth(
            @Param("feId") Long feId,
            @Param("status") OrderStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
