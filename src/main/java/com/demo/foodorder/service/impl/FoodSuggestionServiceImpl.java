package com.demo.foodorder.service.impl;

import com.demo.foodorder.dto.search.FoodSearchResponse;
import com.demo.foodorder.entity.MenuItem;
import com.demo.foodorder.entity.Order;
import com.demo.foodorder.entity.OrderItem;
import com.demo.foodorder.enums.CuisineType;
import com.demo.foodorder.enums.FoodCategory;
import com.demo.foodorder.repository.MenuItemRepository;
import com.demo.foodorder.repository.OrderRepository;
import com.demo.foodorder.service.FoodSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodSuggestionServiceImpl implements FoodSuggestionService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    /**
     * Get personalized food suggestions based on user's order history
     * Uses cuisine and category preferences from past orders
     */
    @Transactional(readOnly = true)
    @Override
    public List<FoodSearchResponse> getSuggestions(Long userId) {

        List<Order> pastOrders = orderRepository.findByConsumerId(userId);
        
        if (pastOrders.isEmpty()) {
            return getPopularItems();
        }

        Map<CuisineType, Long> cuisinePreferences = getCuisinePreferences(pastOrders);
        Map<FoodCategory, Long> categoryPreferences = getCategoryPreferences(pastOrders);
        Set<Long> orderedItemIds = getOrderedItemIds(pastOrders);

        List<MenuItem> allItems = menuItemRepository.findByAvailableTrueAndRestaurantActiveTrue();

        List<ScoredItem> scoredItems = allItems.stream()
                .filter(item -> !orderedItemIds.contains(item.getId())) // Exclude already ordered items
                .map(item -> new ScoredItem(item, calculateScore(item, cuisinePreferences, categoryPreferences)))
                .sorted(Comparator.comparingDouble(ScoredItem::score).reversed())
                .limit(10)
                .toList();

        return scoredItems.stream()
                .map(si -> toFoodSearchResponse(si.item()))
                .collect(Collectors.toList());
    }

    private Map<CuisineType, Long> getCuisinePreferences(List<Order> orders) {
        return orders.stream()
                .flatMap(order -> order.getItems().stream())
                .map(OrderItem::getMenuItem)
                .filter(item -> item.getCuisineType() != null)
                .collect(Collectors.groupingBy(MenuItem::getCuisineType, Collectors.counting()));
    }

    private Map<FoodCategory, Long> getCategoryPreferences(List<Order> orders) {
        return orders.stream()
                .flatMap(order -> order.getItems().stream())
                .map(OrderItem::getMenuItem)
                .filter(item -> item.getFoodCategory() != null)
                .collect(Collectors.groupingBy(MenuItem::getFoodCategory, Collectors.counting()));
    }

    private Set<Long> getOrderedItemIds(List<Order> orders) {
        return orders.stream()
                .flatMap(order -> order.getItems().stream())
                .map(OrderItem::getMenuItem)
                .map(MenuItem::getId)
                .collect(Collectors.toSet());
    }

    private double calculateScore(MenuItem item, 
                                  Map<CuisineType, Long> cuisinePrefs, 
                                  Map<FoodCategory, Long> categoryPrefs) {
        double score = 0.0;

        // Weight for cuisine preference (50%)
        if (item.getCuisineType() != null && cuisinePrefs.containsKey(item.getCuisineType())) {
            score += 0.5 * (cuisinePrefs.get(item.getCuisineType()) / (double) getTotalOrders(cuisinePrefs));
        }

        // Weight for category preference (30%)
        if (item.getFoodCategory() != null && categoryPrefs.containsKey(item.getFoodCategory())) {
            score += 0.3 * (categoryPrefs.get(item.getFoodCategory()) / (double) getTotalOrders(categoryPrefs));
        }

        // Weight for popularity (20%)
        score += 0.2 * (item.getPopularity() / 200.0); 

        return score;
    }

    private long getTotalOrders(Map<?, Long> preferences) {
        return preferences.values().stream().mapToLong(Long::longValue).sum();
    }

    private List<FoodSearchResponse> getPopularItems() {
        return menuItemRepository.findByAvailableTrueAndRestaurantActiveTrue().stream()
                .sorted(Comparator.comparingLong(MenuItem::getPopularity).reversed())
                .limit(10)
                .map(this::toFoodSearchResponse)
                .collect(Collectors.toList());
    }

    private FoodSearchResponse toFoodSearchResponse(MenuItem item) {
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

    private record ScoredItem(MenuItem item, double score) {}
}
