package com.demo.foodorder.repository;

import com.demo.foodorder.entity.Order;
import com.demo.foodorder.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.items oi " +
            "LEFT JOIN FETCH oi.menuItem " +
            "WHERE o.consumer.id = :consumerId " +
            "AND o.consumer.active = true " +
            "AND o.restaurant.active = true")
    List<Order> findByConsumerIdAndActive(@Param("consumerId") Long consumerId);

    List<Order> findByConsumerId(Long consumerId);

    @Query("SELECT o FROM Order o " +
            "WHERE o.restaurant.id = :restaurantId " +
            "AND o.restaurant.active = true")
    List<Order> findByRestaurantIdAndRestaurantActiveTrue(@Param("restaurantId") Long restaurantId);

    List<Order> findByRestaurantIdAndStatus(
            Long restaurantId,
            OrderStatus status
    );

    @Query("SELECT o FROM Order o " +
            "WHERE o.restaurant.id = :restaurantId " +
            "AND o.status = :status " +
            "AND o.restaurant.active = true")
    List<Order> findByRestaurantIdAndStatusAndRestaurantActiveTrue(
            @Param("restaurantId") Long restaurantId,
            @Param("status") OrderStatus status
    );

    Optional<Order> findByIdAndRestaurantId(Long orderId, Long restaurantId);

    @Query("SELECT o FROM Order o " +
            "WHERE o.id = :orderId " +
            "AND o.restaurant.id = :restaurantId " +
            "AND o.restaurant.active = true")
    Optional<Order> findByIdAndRestaurantIdAndRestaurantActiveTrue(
            @Param("orderId") Long orderId,
            @Param("restaurantId") Long restaurantId
    );

    Optional<Order> findByIdAndConsumerId(Long orderId, Long consumerId);

    @Query("SELECT o FROM Order o " +
            "WHERE o.id = :orderId " +
            "AND o.consumer.id = :consumerId " +
            "AND o.consumer.active = true " +
            "AND o.restaurant.active = true")
    Optional<Order> findByIdAndConsumerIdAndActiveTrue(
            @Param("orderId") Long orderId,
            @Param("consumerId") Long consumerId
    );
}
