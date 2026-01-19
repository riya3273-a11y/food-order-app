package com.demo.foodorder.controller;

import com.demo.foodorder.dto.search.RestaurantBrowseResponse;
import com.demo.foodorder.service.RestaurantBrowseService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "6. Consumer - Restaurants", description = "Requires CONSUMER role")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
public class RestaurantBrowseController {

    private final RestaurantBrowseService browseService;

    @Operation(summary = "Browse nearby restaurants with filtering options")
    @GetMapping("/browse")
    public ResponseEntity<List<RestaurantBrowseResponse>> browse(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") double radiusKm,
            @RequestParam(defaultValue = "false") boolean openNow) {

        return ResponseEntity.ok(
                browseService.browseRestaurants(
                        lat, lng, radiusKm, openNow
                )
        );
    }
}
