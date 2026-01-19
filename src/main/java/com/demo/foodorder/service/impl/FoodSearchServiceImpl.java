package com.demo.foodorder.service.impl;

import com.demo.foodorder.dto.search.FoodSearchResponse;
import com.demo.foodorder.enums.CuisineType;
import com.demo.foodorder.enums.FoodCategory;
import com.demo.foodorder.repository.MenuItemRepository;
import com.demo.foodorder.repository.RestaurantTimingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodSearchServiceImpl implements com.demo.foodorder.service.FoodSearchService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantTimingRepository timingRepository;

    @Transactional(readOnly = true)
    @Override
    public List<FoodSearchResponse> searchFoods(
            String q,
            FoodCategory category,
            CuisineType cuisine,
            Boolean vegetarian,
            Boolean vegan,
            Boolean glutenFree,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean openNow) {

        DayOfWeek today =
                DayOfWeek.valueOf(LocalDate.now().getDayOfWeek().name());
        LocalTime now = LocalTime.now();

        return menuItemRepository.searchFoods(
                        q, category, cuisine,
                        vegetarian, vegan, glutenFree,
                        minPrice, maxPrice
                )
                .stream()
                .filter(m -> {
                    if (openNow == null || !openNow) return true;
                    return timingRepository
                            .findByRestaurantIdAndDayOfWeek(
                                    m.getRestaurant().getId(), today
                            )
                            .map(t ->
                                    !now.isBefore(t.getOpenTime()) &&
                                            !now.isAfter(t.getCloseTime())
                            )
                            .orElse(false);
                })
                .map(m -> FoodSearchResponse.builder()
                        .menuItemId(m.getId())
                        .menuItemName(m.getName())
                        .description(m.getDescription())
                        .price(m.getPrice())
                        .category(m.getFoodCategory())
                        .cuisine(m.getCuisineType())
                        .vegetarian(m.getVegetarian())
                        .vegan(m.getVegan())
                        .glutenFree(m.getGlutenFree())
                        .restaurantId(m.getRestaurant().getId())
                        .restaurantName(m.getRestaurant().getName())
                        .build()
                )
                .toList();
    }
}

