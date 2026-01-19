package com.demo.foodorder.dto.menu;

import com.demo.foodorder.enums.CuisineType;
import com.demo.foodorder.enums.FoodCategory;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MenuItemResponse {

    private Long id;
    private String name;
    private String description;
    private FoodCategory foodCategory;
    private CuisineType cuisineType;
    private BigDecimal price;
    private Boolean vegetarian;
    private Boolean vegan;
    private Boolean glutenFree;
    private Boolean available;
}
