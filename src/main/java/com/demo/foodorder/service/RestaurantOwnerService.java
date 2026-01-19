package com.demo.foodorder.service;

import com.demo.foodorder.dto.menu.MenuItemRequest;
import com.demo.foodorder.dto.menu.MenuItemResponse;
import com.demo.foodorder.dto.menu.TimingRequest;
import com.demo.foodorder.dto.menu.TimingResponse;
import com.demo.foodorder.dto.order.OrderResponse;
import com.demo.foodorder.entity.Restaurant;
import com.demo.foodorder.enums.OrderStatus;

import java.util.List;

public interface RestaurantOwnerService {
    Restaurant loadActiveRestaurantForOwner(Long restaurantId, Long ownerId);

    MenuItemResponse addMenuItem(Long restaurantId, MenuItemRequest req, Long ownerId);

    MenuItemResponse updateMenuItem(Long restaurantId, Long itemId, MenuItemRequest req, Long ownerId);

    void deleteMenuItem(Long restaurantId, Long itemId, Long ownerId);

    List<MenuItemResponse> listMenu(Long restaurantId, Long ownerId);

    List<TimingResponse> replaceRestaurantTimings(Long restaurantId, List<TimingRequest> timings, Long ownerId);
    
    // Order management
    List<OrderResponse> getRestaurantOrders(Long restaurantId, Long ownerId);
    
    OrderResponse updateOrderStatus(Long restaurantId, Long orderId, OrderStatus status, Long ownerId);
}
