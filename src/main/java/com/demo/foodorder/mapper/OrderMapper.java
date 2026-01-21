package com.demo.foodorder.mapper;

import com.demo.foodorder.dto.response.OrderItemResponse;
import com.demo.foodorder.dto.response.OrderResponse;
import com.demo.foodorder.entity.Order;

import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .restaurantId(order.getRestaurant().getId())
                .restaurantName(order.getRestaurant().getName())
                .consumerUsername(order.getConsumer().getUsername())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .placedAt(order.getPlacedAt())
                .items(order.getItems().stream()
                        .map(oi -> OrderItemResponse.builder()
                                .menuItemId(oi.getMenuItem().getId())
                                .menuItemName(oi.getMenuItem().getName())
                                .quantity(oi.getQuantity())
                                .price(oi.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
