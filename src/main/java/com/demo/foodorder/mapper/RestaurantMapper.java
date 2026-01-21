package com.demo.foodorder.mapper;

import com.demo.foodorder.dto.response.RestaurantResponse;
import com.demo.foodorder.entity.Restaurant;
import com.demo.foodorder.entity.RestaurantTiming;

public class RestaurantMapper {

    public static RestaurantResponse toResponse(Restaurant r) {
        return baseResponseBuilder(r).build();
    }
    public static RestaurantResponse toResponse(Restaurant r, RestaurantTiming restaurantTiming, Boolean openNow) {
        return  baseResponseBuilder(r)
                .openNow(openNow)
                .timingResponse(RestaurantTimingMapper.toResponse(restaurantTiming))
                .build();
    }
    private static RestaurantResponse.RestaurantResponseBuilder baseResponseBuilder(Restaurant r) {
        return RestaurantResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .address(r.getAddress())
                .description(r.getDescription())
                .latitude(r.getLatitude())
                .longitude(r.getLongitude())
                .ownerEmail(r.getOwner() != null ? r.getOwner().getEmail() : null)
                .active(r.getActive());
    }
}
