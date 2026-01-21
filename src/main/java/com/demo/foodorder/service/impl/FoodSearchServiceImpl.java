package com.demo.foodorder.service.impl;

import com.demo.foodorder.dto.response.FoodSearchResponse;
import com.demo.foodorder.dto.response.PagedFoodSearchResponse;
import com.demo.foodorder.enums.CuisineType;
import com.demo.foodorder.enums.FoodCategory;
import com.demo.foodorder.mapper.FoodSearchMapper;
import com.demo.foodorder.repository.MenuItemRepository;
import com.demo.foodorder.service.FoodSearchService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodSearchServiceImpl implements FoodSearchService {

    private static final Logger logger = LoggerFactory.getLogger(FoodSearchServiceImpl.class);
    private final MenuItemRepository menuItemRepository;

    @Transactional(readOnly = true)
    @Override
    public PagedFoodSearchResponse searchFoods(
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
            int pageSize) {

        if (pageNumber < 0) pageNumber = 0;
        if (pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<com.demo.foodorder.entity.MenuItem> menuItemPage;

        if (openNow != null && openNow) {
            DayOfWeek today = DayOfWeek.valueOf(LocalDate.now().getDayOfWeek().name());
            LocalTime now = LocalTime.now();

            menuItemPage = menuItemRepository.searchFoodsWithOpenNow(
                    q, category, cuisine,
                    vegetarian, vegan, glutenFree,
                    minPrice, maxPrice,
                    today, now,
                    pageable
            );
        } else {
            menuItemPage = menuItemRepository.searchFoods(
                    q, category, cuisine,
                    vegetarian, vegan, glutenFree,
                    minPrice, maxPrice,
                    pageable
            );
        }

        List<FoodSearchResponse> content = menuItemPage.getContent()
                .stream()
                .map(FoodSearchMapper::toResponse)
                .toList();

        return PagedFoodSearchResponse.builder()
                .content(content)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(menuItemPage.getTotalElements())
                .totalPages(menuItemPage.getTotalPages())
                .hasNext(menuItemPage.hasNext())
                .hasPrevious(menuItemPage.hasPrevious())
                .build();
    }
}

