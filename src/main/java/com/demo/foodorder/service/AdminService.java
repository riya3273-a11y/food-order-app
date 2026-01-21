package com.demo.foodorder.service;

import com.demo.foodorder.dto.request.CreateRestaurantRequest;
import com.demo.foodorder.dto.request.UpdateRestaurantRequest;
import com.demo.foodorder.dto.response.RestaurantResponse;
import jakarta.validation.Valid;

public interface AdminService {

    RestaurantResponse createRestaurant(@Valid CreateRestaurantRequest request);

    RestaurantResponse updateRestaurant(Long restaurantId, @Valid UpdateRestaurantRequest request);

    void deleteRestaurant(Long restaurantId);
}
