package com.demo.foodorder.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OrderItemResponse {

    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private BigDecimal price;
}
