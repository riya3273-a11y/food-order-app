package com.demo.foodorder.controller;

import com.demo.foodorder.dto.request.PlaceOrderRequest;
import com.demo.foodorder.dto.response.OrderResponse;
import com.demo.foodorder.security.UserPrincipal;
import com.demo.foodorder.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "6. Consumer - Orders", description = "Requires CONSUMER role")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    @Operation(summary = "Place new order",
            description = "Create a new order with menu items from a restaurant")
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestBody @Valid PlaceOrderRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        logger.info("Placing order for user: {}", principal.getId());
        return ResponseEntity.ok(
                orderService.placeOrder(request, principal.getUser())
        );
    }

    @Operation(summary = "Get my order history",
            description = "View all past orders placed by me")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> myOrders(
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                orderService.getConsumerOrders(principal.getUser())
        );
    }

    @Operation(summary = "Check order status",
            description = "View details and current status of a specific order")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderStatus(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                orderService.getOrderStatus(orderId, principal.getUser())
        );
    }
}
