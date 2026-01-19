package com.demo.foodorder.repository;

import com.demo.foodorder.entity.RestaurantTiming;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface RestaurantTimingRepository extends JpaRepository<RestaurantTiming, Long> {

    List<RestaurantTiming> findByRestaurantId(Long restaurantId);

    Optional<RestaurantTiming> findByRestaurantIdAndDayOfWeek(
            Long restaurantId,
            DayOfWeek dayOfWeek
    );

    void deleteByRestaurantId(Long restaurantId);
}
