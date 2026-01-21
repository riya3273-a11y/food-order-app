package com.demo.foodorder.controller;

import com.demo.foodorder.dto.request.MenuItemRequest;
import com.demo.foodorder.dto.request.TimingRequest;
import com.demo.foodorder.dto.request.UpdateOrderStatusRequest;
import com.demo.foodorder.dto.response.MenuItemResponse;
import com.demo.foodorder.dto.response.OrderResponse;
import com.demo.foodorder.dto.response.RestaurantResponse;
import com.demo.foodorder.dto.response.TimingResponse;
import com.demo.foodorder.enums.OrderStatus;
import com.demo.foodorder.security.UserPrincipal;
import com.demo.foodorder.service.RestaurantOwnerService;
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
@RequestMapping("/api/restaurant-owners/restaurants")
@RequiredArgsConstructor
@Tag(name = "3. Restaurant Owner", description = "Requires RESTAURANT_OWNER role")
@SecurityRequirement(name = "bearerAuth")
public class RestaurantOwnerController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantOwnerController.class);
    private final RestaurantOwnerService ownerService;

    @Operation(summary = "Get all my restaurants",
            description = "View all restaurants owned by the authenticated restaurant owner only")
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getMyRestaurants(
            @AuthenticationPrincipal UserPrincipal principal) {
        logger.info("Getting restaurants for owner: {}", principal.getId());
        return ResponseEntity.ok(
                ownerService.getOwnerRestaurants(principal.getId())
        );
    }

    @Operation(summary = "Add menu item",
            description = "Add a new menu item to the restaurant")
    @PostMapping("/{restaurantId}/menu-items")
    public ResponseEntity<MenuItemResponse> addMenuItem(
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuItemRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        logger.info("Adding menu item to restaurant: {}", restaurantId);
        MenuItemResponse resp = ownerService.addMenuItem(restaurantId, request, principal.getId());
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Update menu item",
            description = "Update details of an existing menu item")
    @PutMapping("/{restaurantId}/menu-items/{itemId}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @Valid @RequestBody MenuItemRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        MenuItemResponse resp = ownerService.updateMenuItem(restaurantId, itemId, request, principal.getId());
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Delete menu item",
            description = "Remove a menu item from the restaurant")
    @DeleteMapping("/{restaurantId}/menu-items/{itemId}")
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserPrincipal principal) {

        ownerService.deleteMenuItem(restaurantId, itemId, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update restaurant timings",
            description = "Replace all restaurant operating hours. Provide timings for each day of the week.")
    @PutMapping("/{restaurantId}/timings")
    public ResponseEntity<List<TimingResponse>> updateTimings(
            @PathVariable Long restaurantId,
            @Valid @RequestBody List<TimingRequest> timings,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                ownerService.updateRestaurantTimings(
                        restaurantId,
                        timings,
                        principal.getId()
                )
        );
    }

    @Operation(summary = "Get restaurant orders",
            description = "View all orders for the restaurant. Optionally filter by status (PLACED, APPROVED, PREPARING, READY_FOR_PICKUP, COMPLETED, CANCELLED, REJECTED)")
    @GetMapping("/{restaurantId}/orders")
    public ResponseEntity<List<OrderResponse>> getOrders(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) OrderStatus status,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                ownerService.getRestaurantOrders(restaurantId, status, principal.getId())
        );
    }

    @Operation(summary = "Approve order",
            description = "Approve a PLACED order. Changes status from PLACED to APPROVED.")
    @PostMapping("/{restaurantId}/orders/{orderId}/approve")
    public ResponseEntity<OrderResponse> approveOrder(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                ownerService.approveOrder(restaurantId, orderId, principal.getId())
        );
    }

    @Operation(summary = "Update order status",
            description = "Change order status (e.g: PREPARING, READY_FOR_PICKUP, COMPLETED)")
    @PatchMapping("/{restaurantId}/orders/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        logger.info("Updating order status: orderId={}, newStatus={}", orderId, request.getStatus());
        return ResponseEntity.ok(
                ownerService.updateOrderStatus(
                        restaurantId,
                        orderId,
                        request.getStatus(),
                        principal.getId()
                )
        );
    }
}
