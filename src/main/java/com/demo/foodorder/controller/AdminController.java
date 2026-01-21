package com.demo.foodorder.controller;

import com.demo.foodorder.dto.request.CreateRestaurantRequest;
import com.demo.foodorder.dto.request.UpdateRestaurantRequest;
import com.demo.foodorder.dto.response.RestaurantResponse;
import com.demo.foodorder.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "2. Admin", description = "Admin APIs - Requires ADMIN role")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;

    @Operation(summary = "Create restaurant account",
            description = "Add restaurant account and link to registered owner. Owner account will be activated.")
    @PostMapping("/restaurants")
    public ResponseEntity<RestaurantResponse> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequest request) {
        logger.info("Creating restaurant: {}", request.getName());
        return ResponseEntity.ok(
                adminService.createRestaurant(request)
        );
    }

    @Operation(summary = "Update restaurant details",
            description = "Update restaurant name, address, description, or location")
    @PutMapping("/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable Long restaurantId,
            @Valid @RequestBody UpdateRestaurantRequest request) {
        logger.info("Updating restaurant: {}", restaurantId);
        return ResponseEntity.ok(
                adminService.updateRestaurant(restaurantId, request)
        );
    }

    @Operation(summary = "Delete restaurant",
            description = "Deactivate restaurant and all its menu items. Owner account deactivated if no other active restaurants.")
    @DeleteMapping("/restaurants/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(
            @PathVariable Long restaurantId) {
        logger.info("Deleting restaurant: {}", restaurantId);
        adminService.deleteRestaurant(restaurantId);
        return ResponseEntity.noContent().build();
    }
}
