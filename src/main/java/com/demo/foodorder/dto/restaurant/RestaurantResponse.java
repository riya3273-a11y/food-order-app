package com.demo.foodorder.dto.restaurant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestaurantResponse {

    private Long id;
    private String name;
    private String address;
    private String description;
    private Double latitude;
    private Double longitude;
    private String ownerEmail;
    private Boolean active;
}
