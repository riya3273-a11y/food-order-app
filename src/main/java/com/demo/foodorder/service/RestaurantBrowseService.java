package com.demo.foodorder.service;

import com.demo.foodorder.dto.search.RestaurantBrowseResponse;

import java.util.List;

public interface RestaurantBrowseService {
    List<RestaurantBrowseResponse> browseRestaurants(
            double userLat,
            double userLng,
            double radiusKm,
            boolean openNowOnly);
}
