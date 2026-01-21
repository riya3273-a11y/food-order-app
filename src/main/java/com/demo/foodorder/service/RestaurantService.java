package com.demo.foodorder.service;

import com.demo.foodorder.dto.response.MenuItemResponse;
import com.demo.foodorder.dto.response.TimingResponse;
import com.demo.foodorder.dto.response.RestaurantResponse;

import java.util.List;

public interface RestaurantService {
    
    List<RestaurantResponse> browseAllRestaurants(boolean openNowOnly);
    
    List<RestaurantResponse> browseRestaurants(
            double userLat,
            double userLng,
            double radiusKm,
            boolean openNow);

    RestaurantResponse getRestaurantById(Long restaurantId);

    List<MenuItemResponse> listMenu(Long restaurantId);

    List<TimingResponse> getRestaurantTimings(Long restaurantId);
}
