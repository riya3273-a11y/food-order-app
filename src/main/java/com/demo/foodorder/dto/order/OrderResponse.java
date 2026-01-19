package com.demo.foodorder.dto.order;

import com.demo.foodorder.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderResponse {

    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private String consumerUsername;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime placedAt;
    private List<OrderItemResponse> items;
}
