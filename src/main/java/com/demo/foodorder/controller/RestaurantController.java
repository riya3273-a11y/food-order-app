package com.demo.foodorder.controller;

import com.demo.foodorder.dto.response.MenuItemResponse;
import com.demo.foodorder.dto.response.TimingResponse;
import com.demo.foodorder.dto.response.RestaurantResponse;
import com.demo.foodorder.security.UserPrincipal;
import com.demo.foodorder.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Tag(name = "4. Restaurants", description = "Browse and view Restaurants details")
public class RestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);
    private final RestaurantService restaurantService;

    @Operation(summary = "Browse all restaurants",
            description = "Get all active restaurants. Optionally filter by openNow (restaurants available at current timing).")
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> browseAll(
            @RequestParam(required = false) Boolean openNow) {
        logger.info("Browsing restaurants");
        return ResponseEntity.ok(
                restaurantService.browseAllRestaurants(
                        Boolean.TRUE.equals(openNow)
                )
        );
    }

    @Operation(summary = "Browse nearby restaurants",
            description = "Find restaurants within a specified radius. Filter by nearby and timing - openNow flag")
    @GetMapping("/nearby")
    public ResponseEntity<List<RestaurantResponse>> browse(
            @Parameter(description = "Latitude") @RequestParam double lat,
            @Parameter(description = "Longitude") @RequestParam double lng,
            @RequestParam(defaultValue = "5") double radiusKm,
            @RequestParam(required = false) Boolean openNow) {
        logger.info("Browsing nearby restaurants: lat={}, lng={}, radius={}km", lat, lng, radiusKm);
        return ResponseEntity.ok(
                restaurantService.browseRestaurants(
                        lat, lng, radiusKm,  Boolean.TRUE.equals(openNow)
                )
        );
    }

    @Operation(summary = "Get restaurant details",
            description = "View details of specific restaurant")
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> getRestaurantById( @PathVariable Long restaurantId) {
        logger.info("Viewing details of a specific restaurant");
        return ResponseEntity.ok(
                restaurantService.getRestaurantById(restaurantId)
        );
    }

    @Operation(summary = "List all menu items",
            description = "View menu items for the restaurant")
    @GetMapping("/{restaurantId}/menu-items")
    public ResponseEntity<List<MenuItemResponse>> listMenu(@PathVariable Long restaurantId) {
        logger.info("Viewing menu of a specific restaurant");
        List<MenuItemResponse> items = restaurantService.listMenu(restaurantId);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "View restaurant timings",
            description = "Get ALL days of the week timings when restaurant is open")
    @GetMapping("/{restaurantId}/timings")
    public ResponseEntity<List<TimingResponse>> getTimings(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal UserPrincipal principal) {
        logger.info("Viewing week timings of a specific restaurant");
        return ResponseEntity.ok(
                restaurantService.getRestaurantTimings(restaurantId)
        );
    }
}
