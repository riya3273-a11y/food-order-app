package com.demo.foodorder.mapper;

import com.demo.foodorder.dto.response.TimingResponse;
import com.demo.foodorder.entity.RestaurantTiming;

public class RestaurantTimingMapper {

    public static TimingResponse toResponse(RestaurantTiming timing) {
        if(timing == null) return null;
        return TimingResponse.builder()
                .dayOfWeek(timing.getDayOfWeek())
                .openTime(timing.getOpenTime())
                .closeTime(timing.getCloseTime())
                .build();
    }
}
