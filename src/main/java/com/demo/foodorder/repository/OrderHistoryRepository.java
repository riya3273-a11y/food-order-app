package com.demo.foodorder.repository;

import com.demo.foodorder.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {

    Optional<OrderHistory> findByUserIdAndMenuItemId(
            Long userId,
            Long menuItemId
    );

    List<OrderHistory> findByUserIdOrderByOrderCountDescLastOrderedAtDesc(
            Long userId
    );
}
