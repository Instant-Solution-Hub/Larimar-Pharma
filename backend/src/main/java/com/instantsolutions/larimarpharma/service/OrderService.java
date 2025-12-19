package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.OrderItemRequestDto;
import com.instantsolutions.larimarpharma.DTOs.OrderRequestDto;
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


    public Order createOrder(Long feId, OrderRequestDto dto) {

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

        return orderRepository.save(order);
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
    public List<Order> getMyOrders(Long feId) {
        return orderRepository.findAllByFieldExecutiveIdAndStatusNot(
                feId, Order.OrderStatus.CANCELLED);
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
