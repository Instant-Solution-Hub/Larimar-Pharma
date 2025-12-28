package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.Order;
import com.instantsolutions.larimarpharma.entity.OrderItem;
import com.instantsolutions.larimarpharma.entity.Product;
import com.instantsolutions.larimarpharma.exceptions.BadRequestException;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.OrderRepository;
import com.instantsolutions.larimarpharma.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
     FieldExecutiveRepository fieldExecutiveRepository;

    @Autowired
    ProductRepository productRepository;


    public OrderResponseDto createOrder(Long feId, OrderRequestDto dto) {

        FieldExecutive fe = fieldExecutiveRepository.findById(feId)
                .orElseThrow(() -> new ResourceNotFoundException("FE not found"));

        Order order = Order.builder()
                .fieldExecutive(fe)
                .institutionName(dto.getInstitutionName())
                .institutionType(dto.getInstitutionType())
                .contactPerson(dto.getContactPerson())
                .contactNumber(dto.getContactNumber())
                .discount(dto.getDiscount())
                .notes(dto.getNotes())
                .status(Order.OrderStatus.PENDING)
                .build();

        Set<OrderItem> items = buildOrderItems(order, dto.getItems());
        order.setOrderItems(items);

         orderRepository.save(order);
         return mapToDto(order);
    }


    public Order updateOrder(Long feId, Long orderId, OrderRequestDto dto) {

        Order order = orderRepository.findByIdAndFieldExecutiveId(orderId, feId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found or not accessible"));

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new BadRequestException("Cancelled order cannot be updated");
        }

        order.setInstitutionName(dto.getInstitutionName());
        order.setInstitutionType(dto.getInstitutionType());
        order.setContactPerson(dto.getContactPerson());
        order.setContactNumber(dto.getContactNumber());
        order.setDiscount(dto.getDiscount());
        order.setNotes(dto.getNotes());

        // Replace order items
        order.getOrderItems().clear();
        order.getOrderItems().addAll(buildOrderItems(order, dto.getItems()));

        return orderRepository.save(order);
    }


    public void cancelOrder(Long feId, Long orderId) {

        Order order = orderRepository.findByIdAndFieldExecutiveId(orderId, feId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found or not accessible"));

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new BadRequestException("Order already cancelled");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }


    @Transactional
    public List<OrderResponseDto> getMyOrders(Long feId) {

        List<Order> orders =
                orderRepository.findAllByFieldExecutiveIdAndStatusNot(
                        feId, Order.OrderStatus.CANCELLED);

        return orders.stream()
                .map(this::mapToDto)
                .toList();
    }

    private OrderResponseDto mapToDto(Order order) {

        return OrderResponseDto.builder()
                .id(order.getId())
                .institutionName(order.getInstitutionName())
                .institutionType(order.getInstitutionType())
                .contactPerson(order.getContactPerson())
                .contactNumber(order.getContactNumber())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .discount(order.getDiscount())
                .totalAmount(order.getTotalAmount())
                .fieldExecutiveId(order.getFieldExecutive().getId())
                .items(
                        order.getOrderItems().stream()
                                .map(item -> OrderItemResponseDto.builder()
                                        .productId(item.getProduct().getId())
                                        .productName(item.getProduct().getName())
                                        .quantity(item.getQuantity())
                                        .price(item.getPrice())
                                        .build())
                                .toList()
                )
                .build();
    }


    @Transactional
    public List<OrderResponseDto> getMyOrdersForCurrentMonth(Long feId) {

        if (feId == null) {
            throw new IllegalArgumentException("Field Executive ID cannot be null");
        }

        fieldExecutiveRepository.findById(feId)
                .orElseThrow(() -> new ResourceNotFoundException("Field Executive not found"));

        LocalDate now = LocalDate.now();
        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);

        List<Order> orders =  orderRepository
                .findAllByFieldExecutiveIdAndOrderDateBetweenOrderByOrderDateDesc(
                        feId,
                        start,
                        end
                );
        return orders.stream()
                .map(this::mapToDto)
                .toList();
    }


    public MonthlyOrderStatsDto getMonthlyOrderStats(Long feId) {

        if (feId == null) {
            throw new IllegalArgumentException("Field Executive ID cannot be null");
        }

        fieldExecutiveRepository.findById(feId)
                .orElseThrow(() -> new ResourceNotFoundException("Field Executive not found"));

        LocalDate now = LocalDate.now();
        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);

        long totalOrders =
                orderRepository.countByFieldExecutiveIdAndOrderDateBetweenAndStatusNot(
                        feId, start, end, Order.OrderStatus.CANCELLED);

        long pendingOrders =
                orderRepository.countByFieldExecutiveIdAndOrderDateBetweenAndStatus(
                        feId, start, end, Order.OrderStatus.PENDING);

        Double totalSales =
                orderRepository.sumTotalAmountByStatusForMonth(
                        feId, Order.OrderStatus.CONFIRMED, start, end);

        return MonthlyOrderStatsDto.builder()
                .fieldExecutiveId(feId)
                .year(now.getYear())
                .month(now.getMonthValue())
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .totalSales(totalSales)
                .build();
    }

    // ---------- helper ----------
    private Set<OrderItem> buildOrderItems(
            Order order, Set<OrderItemRequestDto> items) {

        if (items == null || items.isEmpty()) {
            throw new BadRequestException("Order must contain at least one item");
        }

        return items.stream().map(i -> {
            Product product = productRepository.findById(i.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found: " + i.getProductId()));

            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(i.getQuantity())
                    .price(i.getPrice())
                    .build();
        }).collect(Collectors.toSet());
    }
}
