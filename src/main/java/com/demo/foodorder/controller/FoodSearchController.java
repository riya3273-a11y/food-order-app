package com.demo.foodorder.controller;

import com.demo.foodorder.dto.response.PagedFoodSearchResponse;
import com.demo.foodorder.enums.CuisineType;
import com.demo.foodorder.enums.FoodCategory;
import com.demo.foodorder.service.FoodSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "5. Search Foods", description = "Search Foods")
public class FoodSearchController {

    private static final Logger logger = LoggerFactory.getLogger(FoodSearchController.class);
    private final FoodSearchService foodSearchService;

    @Operation(summary = "Search food items with filters (category, cuisine, dietary, price, openNow) and pagination")
    @GetMapping("/foods")
    public ResponseEntity<PagedFoodSearchResponse> searchFoods(
            @Parameter(description = "Search by name") @RequestParam(required = false) String q,
            @RequestParam(required = false) FoodCategory category,
            @RequestParam(required = false) CuisineType cuisine,
            @RequestParam(required = false) Boolean vegetarian,
            @RequestParam(required = false) Boolean vegan,
            @RequestParam(required = false) Boolean glutenFree,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean openNow,
            @Parameter(description = "Page number (0-indexed)", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (max 100)", example = "10") 
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Searching foods with query: {}, page: {}, size: {}", q, page, size);
        return ResponseEntity.ok(
                foodSearchService.searchFoods(
                        q, category, cuisine,
                        vegetarian, vegan, glutenFree,
                        minPrice, maxPrice, openNow,
                        page, size
                )
        );
    }
}
