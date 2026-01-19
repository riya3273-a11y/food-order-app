package com.demo.foodorder.dto.restaurant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRestaurantRequest {

    private String name;
    private String address;
    private String description;
    private Double latitude;
    private Double longitude;
    private Boolean active;
}
