package com.demo.foodorder.controller;

import com.demo.foodorder.dto.restaurant.CreateRestaurantRequest;
import com.demo.foodorder.dto.restaurant.RestaurantResponse;
import com.demo.foodorder.dto.restaurant.UpdateRestaurantRequest;
import com.demo.foodorder.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "2. Admin", description = "Admin APIs - Requires ADMIN role")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/restaurants")
    @Operation(summary = "Create restaurant account",
            description = "Add Restaurant restaurant account and link and approve registered owner")
    public ResponseEntity<RestaurantResponse> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequest request) {

        return ResponseEntity.ok(
                adminService.createRestaurant(request)
        );
    }

    @PutMapping("/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable Long restaurantId,
            @Valid @RequestBody UpdateRestaurantRequest request) {

        return ResponseEntity.ok(
                adminService.updateRestaurant(restaurantId, request)
        );
    }

    @DeleteMapping("/restaurants/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(
            @PathVariable Long restaurantId) {

        adminService.deleteRestaurant(restaurantId);
        return ResponseEntity.noContent().build();
    }
}
