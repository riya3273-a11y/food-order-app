package com.demo.foodorder.service.impl;

import com.demo.foodorder.dto.response.FoodSearchResponse;
import com.demo.foodorder.entity.MenuItem;
import com.demo.foodorder.entity.Order;
import com.demo.foodorder.entity.OrderItem;
import com.demo.foodorder.enums.CuisineType;
import com.demo.foodorder.enums.FoodCategory;
import com.demo.foodorder.mapper.FoodSearchMapper;
import com.demo.foodorder.repository.MenuItemRepository;
import com.demo.foodorder.repository.OrderRepository;
import com.demo.foodorder.service.FoodSuggestionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodSuggestionServiceImpl implements FoodSuggestionService {

    private static final Logger logger = LoggerFactory.getLogger(FoodSuggestionServiceImpl.class);
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    private static final double WEIGHT_CUISINE = 0.50;
    private static final double WEIGHT_CATEGORY = 0.30;
    private static final double WEIGHT_POPULARITY = 0.15;
    private static final double WEIGHT_RECENCY = 0.05;

    private static final int RECENCY_DAYS = 15;
    private static final long REPEAT_SUGGESTION_POPULARITY_THRESHOLD = 3L;

    @Transactional(readOnly = true)
    @Override
    public List<FoodSearchResponse> getSuggestions(Long userId) {
        logger.info("Getting food suggestions for user: {}", userId);
        final List<Order> pastOrders = orderRepository.findByConsumerIdAndActive(userId);
        final Map<Long, Long> itemPopularity = getItemPopularity();

        if (pastOrders.isEmpty()) {
            return getPopularItems(itemPopularity);
        }

        final Map<CuisineType, Long> cuisinePreferences = getCuisinePreferences(pastOrders);
        final Map<FoodCategory, Long> categoryPreferences = getCategoryPreferences(pastOrders);
        final Set<Long> orderedItemIds = getOrderedItemIds(pastOrders);

        final List<MenuItem> allItems =
                menuItemRepository.findByAvailableTrueAndRestaurantActiveTrue();

        final List<MenuItem> initialCandidates = allItems.stream()
                .filter((MenuItem item) -> {
                    long globalCount = itemPopularity.getOrDefault(item.getId(), 0L);
                    boolean repeatPopular = globalCount >= REPEAT_SUGGESTION_POPULARITY_THRESHOLD;
                    return !orderedItemIds.contains(item.getId()) || repeatPopular;
                })
                .collect(Collectors.toList());

        final List<MenuItem> candidates =
                initialCandidates.isEmpty() ? allItems : initialCandidates;

        final long maxOrderCount = itemPopularity.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);

        final double tmpMaxPopularityLog = Math.log1p(maxOrderCount);
        final double maxPopularityLog =
                tmpMaxPopularityLog <= 0 ? 1.0 : tmpMaxPopularityLog;

        final List<ScoredItem> scoredItems = candidates.stream()
                .map(item -> new ScoredItem(
                        item,
                        calculateScore(
                                item,
                                cuisinePreferences,
                                categoryPreferences,
                                itemPopularity,
                                pastOrders,
                                maxPopularityLog
                        )
                ))
                .sorted(Comparator.comparingDouble(ScoredItem::score).reversed())
                .limit(10)
                .toList();

        if (scoredItems.isEmpty()) {
            return getPopularItems(itemPopularity);
        }

        return scoredItems.stream()
                .map(si -> FoodSearchMapper.toResponse(si.item()))
                .collect(Collectors.toList());
    }

    private Map<CuisineType, Long> getCuisinePreferences(List<Order> orders) {
        return orders.stream()
                .flatMap(o -> o.getItems().stream())
                .map(OrderItem::getMenuItem)
                .filter(i -> i.getCuisineType() != null)
                .collect(Collectors.groupingBy(
                        MenuItem::getCuisineType,
                        Collectors.counting()
                ));
    }

    private Map<FoodCategory, Long> getCategoryPreferences(List<Order> orders) {
        return orders.stream()
                .flatMap(o -> o.getItems().stream())
                .map(OrderItem::getMenuItem)
                .filter(i -> i.getFoodCategory() != null)
                .collect(Collectors.groupingBy(
                        MenuItem::getFoodCategory,
                        Collectors.counting()
                ));
    }

    private Set<Long> getOrderedItemIds(List<Order> orders) {
        return orders.stream()
                .flatMap(o -> o.getItems().stream())
                .map(OrderItem::getMenuItem)
                .map(MenuItem::getId)
                .collect(Collectors.toSet());
    }

    private Map<Long, Long> getItemPopularity() {
        return menuItemRepository.findMenuItemsWithOrderCount().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    private double calculateScore(
            MenuItem item,
            Map<CuisineType, Long> cuisinePrefs,
            Map<FoodCategory, Long> categoryPrefs,
            Map<Long, Long> itemPopularity,
            List<Order> pastOrders,
            double maxPopularityLog
    ) {
        double score = 0.0;

        long cuisineTotal = safeTotal(cuisinePrefs);
        long categoryTotal = safeTotal(categoryPrefs);

        if (item.getCuisineType() != null) {
            score += WEIGHT_CUISINE *
                    safeFraction(
                            cuisinePrefs.getOrDefault(item.getCuisineType(), 0L),
                            cuisineTotal
                    );
        }

        if (item.getFoodCategory() != null) {
            score += WEIGHT_CATEGORY *
                    safeFraction(
                            categoryPrefs.getOrDefault(item.getFoodCategory(), 0L),
                            categoryTotal
                    );
        }

        long orderCount = itemPopularity.getOrDefault(item.getId(), 0L);
        double popularityNormalized = Math.log1p(orderCount) / maxPopularityLog;
        score += WEIGHT_POPULARITY * popularityNormalized;

        score += WEIGHT_RECENCY * computeRecencyBoost(item, pastOrders);

        return score;
    }

    private double safeFraction(long numerator, long denominator) {
        return denominator <= 0 ? 0.0 : numerator / (double) denominator;
    }

    private long safeTotal(Map<?, Long> map) {
        return map == null || map.isEmpty()
                ? 0L
                : map.values().stream().mapToLong(Long::longValue).sum();
    }

    private double computeRecencyBoost(MenuItem item, List<Order> pastOrders) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(RECENCY_DAYS);

        boolean exactRecent = pastOrders.stream()
                .filter(o -> o.getPlacedAt() != null && o.getPlacedAt().isAfter(cutoff))
                .flatMap(o -> o.getItems().stream())
                .map(OrderItem::getMenuItem)
                .anyMatch(mi -> mi != null && mi.getId().equals(item.getId()));

        if (exactRecent) return 1.0;

        if (item.getCuisineType() != null) {
            boolean cuisineRecent = pastOrders.stream()
                    .filter(o -> o.getPlacedAt() != null && o.getPlacedAt().isAfter(cutoff))
                    .flatMap(o -> o.getItems().stream())
                    .map(OrderItem::getMenuItem)
                    .anyMatch(mi -> mi != null &&
                            item.getCuisineType().equals(mi.getCuisineType()));
            if (cuisineRecent) return 0.5;
        }

        return 0.0;
    }

    private List<FoodSearchResponse> getPopularItems(Map<Long, Long> itemPopularity) {
        return menuItemRepository.findByAvailableTrueAndRestaurantActiveTrue().stream()
                .sorted(Comparator.comparingLong(
                        (MenuItem m) -> itemPopularity.getOrDefault(m.getId(), 0L)
                ).reversed())
                .limit(10)
                .map(FoodSearchMapper::toResponse)
                .collect(Collectors.toList());
    }

    private record ScoredItem(MenuItem item, double score) {}
}
