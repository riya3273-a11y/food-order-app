package com.demo.foodorder.service;

import com.demo.foodorder.dto.search.FoodSearchResponse;
import com.demo.foodorder.enums.CuisineType;
import com.demo.foodorder.enums.FoodCategory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface FoodSearchService {
    List<FoodSearchResponse> searchFoods(
            String q,
            FoodCategory category,
            CuisineType cuisine,
            Boolean vegetarian,
            Boolean vegan,
            Boolean glutenFree,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean openNow);
}
