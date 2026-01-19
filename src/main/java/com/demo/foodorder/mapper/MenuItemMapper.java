package com.demo.foodorder.mapper;

import com.demo.foodorder.dto.menu.MenuItemResponse;
import com.demo.foodorder.entity.MenuItem;

public class MenuItemMapper {

    public static MenuItemResponse toResponse(MenuItem m) {
        return MenuItemResponse.builder()
                .id(m.getId())
//                .restaurantId(m.getRestaurant().getId())
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
