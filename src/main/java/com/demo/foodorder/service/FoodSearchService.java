package com.demo.foodorder.service;

import com.demo.foodorder.dto.response.PagedFoodSearchResponse;
import com.demo.foodorder.enums.CuisineType;
import com.demo.foodorder.enums.FoodCategory;

import java.math.BigDecimal;

public interface FoodSearchService {
    PagedFoodSearchResponse searchFoods(
            String q,
            FoodCategory category,
            CuisineType cuisine,
            Boolean vegetarian,
            Boolean vegan,
            Boolean glutenFree,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean openNow,
            int pageNumber,
            int pageSize);
}
