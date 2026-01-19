package com.demo.foodorder.controller;

import com.demo.foodorder.dto.order.OrderResponse;
import com.demo.foodorder.dto.order.PlaceOrderRequest;
import com.demo.foodorder.security.UserPrincipal;
import com.demo.foodorder.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "4. Consumer - Orders", description = "Requires CONSUMER role")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Place new order")
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestBody @Valid PlaceOrderRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                orderService.placeOrder(request, principal.getUser())
        );
    }

    @Operation(summary = "Get all my orders (order history)")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> myOrders(
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                orderService.getConsumerOrders(principal.getUser())
        );
    }

    @Operation(summary = "Check specific order status")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderStatus(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                orderService.getOrderStatus(orderId, principal.getUser())
        );
    }
}
