package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Order;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponseDto {

    private Long id;
    private String institutionName;
    private Order.InstitutionType institutionType;
    private String contactPerson;
    private String contactNumber;
    private LocalDateTime orderDate;
    private Order.OrderStatus status;
    private Double discount;
    private Double totalAmount;

    private Long fieldExecutiveId;

    private List<OrderItemResponseDto> items;
}
