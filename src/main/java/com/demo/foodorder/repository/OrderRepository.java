package com.demo.foodorder.repository;

import com.demo.foodorder.entity.Order;
import com.demo.foodorder.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByConsumerId(Long consumerId);

    List<Order> findByRestaurantId(Long restaurantId);

    List<Order> findByRestaurantIdAndStatus(
            Long restaurantId,
            OrderStatus status
    );

    Optional<Order> findByIdAndRestaurantId(Long orderId, Long restaurantId);

    Optional<Order> findByIdAndConsumerId(Long orderId, Long consumerId);
}
