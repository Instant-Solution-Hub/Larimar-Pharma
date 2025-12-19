package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.MonthlyOrderStatsDto;
import com.instantsolutions.larimarpharma.DTOs.OrderRequestDto;
import com.instantsolutions.larimarpharma.entity.Order;
import com.instantsolutions.larimarpharma.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fe/orders")
@RequiredArgsConstructor
public class FEOrderController {

    @Autowired
    OrderService orderService;

    // 🔐 feId should ideally come from JWT, kept simple here
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestParam Long feId,
            @RequestBody OrderRequestDto dto) {

        return ResponseEntity.ok(
                orderService.createOrder(feId, dto)
        );
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(
            @RequestParam Long feId,
            @PathVariable Long orderId,
            @RequestBody OrderRequestDto dto) {

        return ResponseEntity.ok(
                orderService.updateOrder(feId, orderId, dto)
        );
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @RequestParam Long feId,
            @PathVariable Long orderId) {

        orderService.cancelOrder(feId, orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders(
            @RequestParam Long feId) {

        return ResponseEntity.ok(
                orderService.getMyOrders(feId)
        );
    }

    // 🔹 ALL orders (including cancelled) for current month
    @GetMapping("/{feId}/current-month")
    public ResponseEntity<ApiResponseDto<List<Order>>> getMyOrdersForCurrentMonth(
            @PathVariable Long feId) {

        List<Order> orders =
                orderService.getMyOrdersForCurrentMonth(feId);

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        orders,
                        "Current month orders fetched successfully"
                )
        );
    }

    @GetMapping("/monthly-stats/{feId}")
    public ResponseEntity<ApiResponseDto<MonthlyOrderStatsDto>> getMonthlyOrderStats(
            @PathVariable Long feId) {

        MonthlyOrderStatsDto stats =
                orderService.getMonthlyOrderStats(feId);

        return ResponseEntity.ok(
                ApiResponseDto.success(stats, "Monthly order stats fetched successfully")
        );
    }
}
