package com.demo.foodorder.mapper;

import com.demo.foodorder.dto.restaurant.RestaurantResponse;
import com.demo.foodorder.entity.Restaurant;

public class RestaurantMapper {

    public static RestaurantResponse toResponse(Restaurant r) {
        return RestaurantResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .address(r.getAddress())
                .description(r.getDescription())
                .latitude(r.getLatitude())
                .longitude(r.getLongitude())
                .ownerEmail(r.getOwner() != null ? r.getOwner().getEmail() : null)
                .active(r.getActive())
                .build();
    }
}
