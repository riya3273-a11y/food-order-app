package com.demo.foodorder.repository;

import com.demo.foodorder.entity.MenuItem;
import com.demo.foodorder.enums.CuisineType;
import com.demo.foodorder.enums.FoodCategory;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MenuItemRepository
        extends JpaRepository<MenuItem, Long>,
        JpaSpecificationExecutor<MenuItem> {

    @Query("""
                SELECT m FROM MenuItem m
                JOIN m.restaurant r
                WHERE m.restaurant.id = :restaurantId
                  AND r.active = true
                  AND m.available = true
            """)
    List<MenuItem> findByRestaurantIdAndAvailableTrue(@Param("restaurantId") Long restaurantId);

    @Query("""
                SELECT m FROM MenuItem m
                JOIN m.restaurant r
                WHERE r.active = true
                  AND m.available = true
            """)
    List<MenuItem> findByAvailableTrueAndRestaurantActiveTrue();

    @Query("""
                SELECT m.id, COUNT(oi.id) 
                FROM MenuItem m
                LEFT JOIN OrderItem oi ON oi.menuItem.id = m.id
                WHERE m.restaurant.active = true
                  AND m.available = true
                GROUP BY m.id
            """)
    List<Object[]> findMenuItemsWithOrderCount();

    void deleteByRestaurantId(Long restaurantId);

    Optional<MenuItem> findByIdAndRestaurantId(Long id, Long restaurantId);

    @Query("""
                SELECT m FROM MenuItem m
                JOIN m.restaurant r
                WHERE m.id = :itemId
                  AND m.restaurant.id = :restaurantId
                  AND m.available = true
                  AND r.active = true
            """)
    Optional<MenuItem> findByIdAndRestaurantIdAndAvailableTrue(
            @Param("itemId") Long itemId,
            @Param("restaurantId") Long restaurantId
    );

    boolean existsByRestaurantIdAndNameIgnoreCase(Long restaurantId, @NotBlank String name);

    boolean existsByRestaurantIdAndNameIgnoreCaseAndAvailableTrue(
            Long restaurantId,
            @NotBlank String name
    );

    @Query("""
                SELECT m FROM MenuItem m
                JOIN m.restaurant r
                WHERE r.active = true
                  AND m.available = true
                  AND (:q IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :q, '%')))
                  AND (:category IS NULL OR m.foodCategory = :category)
                  AND (:cuisine IS NULL OR m.cuisineType = :cuisine)
                  AND (:vegetarian IS NULL OR m.vegetarian = :vegetarian)
                  AND (:vegan IS NULL OR m.vegan = :vegan)
                  AND (:glutenFree IS NULL OR m.glutenFree = :glutenFree)
                  AND (:minPrice IS NULL OR m.price >= :minPrice)
                  AND (:maxPrice IS NULL OR m.price <= :maxPrice)
            """)
    Page<MenuItem> searchFoods(
            @Param("q") String q,
            @Param("category") FoodCategory category,
            @Param("cuisine") CuisineType cuisine,
            @Param("vegetarian") Boolean vegetarian,
            @Param("vegan") Boolean vegan,
            @Param("glutenFree") Boolean glutenFree,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    @Query("""
                SELECT m FROM MenuItem m
                JOIN m.restaurant r
                LEFT JOIN RestaurantTiming rt ON rt.restaurant.id = r.id AND rt.dayOfWeek = :dayOfWeek
                WHERE r.active = true
                  AND m.available = true
                  AND (:q IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :q, '%')))
                  AND (:category IS NULL OR m.foodCategory = :category)
                  AND (:cuisine IS NULL OR m.cuisineType = :cuisine)
                  AND (:vegetarian IS NULL OR m.vegetarian = :vegetarian)
                  AND (:vegan IS NULL OR m.vegan = :vegan)
                  AND (:glutenFree IS NULL OR m.glutenFree = :glutenFree)
                  AND (:minPrice IS NULL OR m.price >= :minPrice)
                  AND (:maxPrice IS NULL OR m.price <= :maxPrice)
                  AND (rt IS NOT NULL AND :currentTime >= rt.openTime AND :currentTime <= rt.closeTime)
            """)
    Page<MenuItem> searchFoodsWithOpenNow(
            @Param("q") String q,
            @Param("category") FoodCategory category,
            @Param("cuisine") CuisineType cuisine,
            @Param("vegetarian") Boolean vegetarian,
            @Param("vegan") Boolean vegan,
            @Param("glutenFree") Boolean glutenFree,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("dayOfWeek") java.time.DayOfWeek dayOfWeek,
            @Param("currentTime") java.time.LocalTime currentTime,
            Pageable pageable
    );
}
