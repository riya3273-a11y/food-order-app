package com.demo.foodorder.service;

import com.demo.foodorder.dto.restaurant.CreateRestaurantRequest;
import com.demo.foodorder.dto.restaurant.RestaurantResponse;
import com.demo.foodorder.dto.restaurant.UpdateRestaurantRequest;
import jakarta.validation.Valid;

public interface AdminService {

    RestaurantResponse createRestaurant(@Valid CreateRestaurantRequest request);

    RestaurantResponse updateRestaurant(Long restaurantId, @Valid UpdateRestaurantRequest request);

    void deleteRestaurant(Long restaurantId);
}
