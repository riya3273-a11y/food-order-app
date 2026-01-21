package com.demo.foodorder.mapper;

import com.demo.foodorder.dto.response.MenuItemResponse;
import com.demo.foodorder.entity.MenuItem;

public class MenuItemMapper {

    public static MenuItemResponse toResponse(MenuItem m) {
        return MenuItemResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .description(m.getDescription())
                .foodCategory(m.getFoodCategory())
                .cuisineType(m.getCuisineType())
                .price(m.getPrice())
                .vegetarian(m.getVegetarian())
                .vegan(m.getVegan())
                .glutenFree(m.getGlutenFree())
                .available(m.getAvailable())
                .build();
    }
}
