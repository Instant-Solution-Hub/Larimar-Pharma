package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.Order;
import com.instantsolutions.larimarpharma.entity.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndFieldExecutiveId(Long orderId, Long feId);

    List<Order> findAllByFieldExecutiveId(Long feId);

    boolean existsByIdAndFieldExecutiveId(Long orderId, Long feId);

    List<Order> findAllByFieldExecutiveIdAndStatusNot(
            Long feId, OrderStatus status
    );
}
