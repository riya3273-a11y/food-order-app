package com.demo.foodorder.dto.search;

import com.demo.foodorder.enums.CuisineType;
import com.demo.foodorder.enums.FoodCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class FoodSearchResponse {
    private Long menuItemId;
    private String menuItemName;
    private String description;
    private BigDecimal price;
    private FoodCategory category;
    private CuisineType cuisine;
    private Boolean vegetarian;
    private Boolean vegan;
    private Boolean glutenFree;
    private Long restaurantId;
    private String restaurantName;
}

