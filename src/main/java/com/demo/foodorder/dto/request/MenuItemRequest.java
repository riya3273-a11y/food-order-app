package com.demo.foodorder.dto.request;


import com.demo.foodorder.enums.CuisineType;
import com.demo.foodorder.enums.FoodCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MenuItemRequest {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private FoodCategory category;

    @NotNull
    private CuisineType cuisine;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @NotNull
    private Boolean available = true;

    @NotNull
    private Boolean vegetarian = false;

    @NotNull
    private Boolean vegan = false;

    @NotNull
    private Boolean glutenFree = false;
}
