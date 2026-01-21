package com.demo.foodorder.service;

import com.demo.foodorder.dto.request.MenuItemRequest;
import com.demo.foodorder.dto.request.TimingRequest;
import com.demo.foodorder.dto.response.MenuItemResponse;
import com.demo.foodorder.dto.response.OrderResponse;
import com.demo.foodorder.dto.response.RestaurantResponse;
import com.demo.foodorder.dto.response.TimingResponse;
import com.demo.foodorder.enums.OrderStatus;

import java.util.List;

public interface RestaurantOwnerService {

    List<RestaurantResponse> getOwnerRestaurants(Long ownerId);

    MenuItemResponse addMenuItem(Long restaurantId, MenuItemRequest req, Long ownerId);

    MenuItemResponse updateMenuItem(Long restaurantId, Long itemId, MenuItemRequest req, Long ownerId);

    void deleteMenuItem(Long restaurantId, Long itemId, Long ownerId);

    List<TimingResponse> updateRestaurantTimings(Long restaurantId, List<TimingRequest> timings, Long ownerId);

    List<OrderResponse> getRestaurantOrders(Long restaurantId, OrderStatus status, Long ownerId);

    OrderResponse approveOrder(Long restaurantId, Long orderId, Long ownerId);

    OrderResponse updateOrderStatus(Long restaurantId, Long orderId, OrderStatus status, Long ownerId);
}
