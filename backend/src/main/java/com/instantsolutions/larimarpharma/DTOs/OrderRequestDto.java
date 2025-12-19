package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Order;
import lombok.Data;

import java.util.Set;

@Data
public class OrderRequestDto {
    private String institutionName;
    private Order.InstitutionType institutionType;
    private String contactPerson;
    private String contactNumber;
    private Double discount;
    private String notes;
    private Set<OrderItemRequestDto> items;
}

