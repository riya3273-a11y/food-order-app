package com.demo.foodorder.mapper;

import com.demo.foodorder.dto.response.FoodSearchResponse;
import com.demo.foodorder.entity.MenuItem;

public class FoodSearchMapper {

    public static FoodSearchResponse toResponse(MenuItem item) {
        return FoodSearchResponse.builder()
                .menuItemId(item.getId())
                .menuItemName(item.getName())
                .description(item.getDescription())
                .category(item.getFoodCategory())
                .cuisine(item.getCuisineType())
                .price(item.getPrice())
                .vegetarian(item.getVegetarian())
                .vegan(item.getVegan())
                .glutenFree(item.getGlutenFree())
                .restaurantId(item.getRestaurant().getId())
                .restaurantName(item.getRestaurant().getName())
                .build();
    }
}
