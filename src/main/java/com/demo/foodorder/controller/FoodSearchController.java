package com.demo.foodorder.controller;

import com.demo.foodorder.dto.search.FoodSearchResponse;
import com.demo.foodorder.enums.CuisineType;
import com.demo.foodorder.enums.FoodCategory;
import com.demo.foodorder.service.FoodSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "5. Consumer - Search", description = "Requires CONSUMER role")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
public class FoodSearchController {

    private final FoodSearchService foodSearchService;

    @Operation(summary = "Search food items with filters (category, cuisine, dietary, price)")
    @GetMapping("/foods")
    public ResponseEntity<List<FoodSearchResponse>> searchFoods(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) FoodCategory category,
            @RequestParam(required = false) CuisineType cuisine,
            @RequestParam(required = false) Boolean vegetarian,
            @RequestParam(required = false) Boolean vegan,
            @RequestParam(required = false) Boolean glutenFree,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean openNow) {

        return ResponseEntity.ok(
                foodSearchService.searchFoods(
                        q, category, cuisine,
                        vegetarian, vegan, glutenFree,
                        minPrice, maxPrice, openNow
                )
        );
    }
}
