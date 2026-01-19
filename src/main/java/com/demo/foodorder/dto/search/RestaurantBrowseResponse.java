package com.demo.foodorder.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RestaurantBrowseResponse {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private boolean openNow;
}

