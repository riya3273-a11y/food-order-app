package com.demo.foodorder.repository;

import com.demo.foodorder.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByActiveTrue();

    Optional<Restaurant> findByIdAndActiveTrue(Long id);

    List<Restaurant> findByOwnerId(Long ownerId);

    boolean existsByNameIgnoreCaseAndLatitudeAndLongitude(String name, Double latitude, Double longitude);

    long countByOwnerIdAndActiveTrue(Long id);

    Optional<Restaurant> findByIdAndOwnerIdAndActiveTrue(Long restaurantId, Long ownerId);
}
