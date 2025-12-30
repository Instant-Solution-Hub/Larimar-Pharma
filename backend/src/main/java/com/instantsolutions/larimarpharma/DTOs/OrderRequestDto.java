package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Order;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.Set;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
public class OrderRequestDto {

    @NotBlank(message = "Institution name is required")
    private String institutionName;

    @NotNull(message = "Institution type is required")
    private Order.InstitutionType institutionType;

    @NotBlank(message = "Contact person is required")
    private String contactPerson;

    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid contact number"
    )
    private String contactNumber;

    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    private Double discount;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @NotEmpty(message = "Order must contain at least one item")
    private Set<@Valid OrderItemRequestDto> items;
}


