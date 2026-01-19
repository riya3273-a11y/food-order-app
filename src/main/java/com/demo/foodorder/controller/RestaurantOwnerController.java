package com.demo.foodorder.controller;

import com.demo.foodorder.dto.menu.MenuItemRequest;
import com.demo.foodorder.dto.menu.MenuItemResponse;
import com.demo.foodorder.dto.menu.TimingRequest;
import com.demo.foodorder.dto.menu.TimingResponse;
import com.demo.foodorder.dto.order.OrderResponse;
import com.demo.foodorder.dto.order.UpdateOrderStatusRequest;
import com.demo.foodorder.security.UserPrincipal;
import com.demo.foodorder.service.RestaurantOwnerService;
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
@RequestMapping("/api/restaurant-owners")
@RequiredArgsConstructor
@Tag(name = "3. Restaurant Owner", description = "Requires RESTAURANT_OWNER role")
@SecurityRequirement(name = "bearerAuth")
public class RestaurantOwnerController {

    private final RestaurantOwnerService ownerService;

    @Operation(summary = "Add menu item for the restaurant (owner)")
    @PostMapping("/{restaurantId}/menu")
    public ResponseEntity<MenuItemResponse> addMenuItem(
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuItemRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        MenuItemResponse resp = ownerService.addMenuItem(restaurantId, request, principal.getId());
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Update menu item")
    @PutMapping("/{restaurantId}/menu/{itemId}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @Valid @RequestBody MenuItemRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        MenuItemResponse resp = ownerService.updateMenuItem(restaurantId, itemId, request, principal.getId());
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Delete menu item")
    @DeleteMapping("/{restaurantId}/menu/{itemId}")
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserPrincipal principal) {

        ownerService.deleteMenuItem(restaurantId, itemId, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List active menu items")
    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<MenuItemResponse>> listMenu(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal UserPrincipal principal) {

        List<MenuItemResponse> items = ownerService.listMenu(restaurantId, principal.getId());
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Replace restaurant timings (mon..sun). Owner only.")
    @PutMapping("/{restaurantId}/timings")
    public ResponseEntity<List<TimingResponse>> updateTimings(
            @PathVariable Long restaurantId,
            @Valid @RequestBody List<TimingRequest> timings,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                ownerService.replaceRestaurantTimings(
                        restaurantId,
                        timings,
                        principal.getId()
                )
        );
    }

    @Operation(summary = "Get all orders for restaurant")
    @GetMapping("/{restaurantId}/orders")
    public ResponseEntity<List<OrderResponse>> getOrders(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                ownerService.getRestaurantOrders(restaurantId, principal.getId())
        );
    }

    @Operation(summary = "Update order status (approve/prepare/ready)")
    @PatchMapping("/{restaurantId}/orders/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

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
