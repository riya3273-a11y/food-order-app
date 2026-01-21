package com.demo.foodorder.dto.request;

import com.demo.foodorder.enums.CuisineType;
import com.demo.foodorder.enums.FoodCategory;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FoodSearchRequest {

    private String query;
    private CuisineType cuisineType;
    private FoodCategory foodCategory;
    private Boolean vegetarian;
    private Boolean vegan;
    private Boolean glutenFree;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean openNow;
}
