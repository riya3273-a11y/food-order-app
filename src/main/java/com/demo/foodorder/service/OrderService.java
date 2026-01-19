package com.demo.foodorder.service;

import com.demo.foodorder.dto.order.OrderResponse;
import com.demo.foodorder.dto.order.PlaceOrderRequest;
import com.demo.foodorder.entity.User;

import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(
            PlaceOrderRequest request,
            User user);

    List<OrderResponse> getConsumerOrders(User user);
    
    OrderResponse getOrderStatus(Long orderId, User user);
}
