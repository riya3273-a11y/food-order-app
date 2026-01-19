package com.demo.foodorder.service.impl;

import com.demo.foodorder.dto.order.OrderItemRequest;
import com.demo.foodorder.dto.order.OrderResponse;
import com.demo.foodorder.dto.order.PlaceOrderRequest;
import com.demo.foodorder.entity.*;
import com.demo.foodorder.exception.ResourceNotFoundException;
import com.demo.foodorder.mapper.OrderMapper;
import com.demo.foodorder.repository.MenuItemRepository;
import com.demo.foodorder.repository.OrderRepository;
import com.demo.foodorder.repository.RestaurantRepository;
import com.demo.foodorder.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public OrderResponse placeOrder(
            PlaceOrderRequest request,
            User user) {

        Restaurant restaurant =
                restaurantRepository.findByIdAndActiveTrue(request.getRestaurantId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Restaurant not found"));

        Order order = new Order();
        order.setConsumer(user);
        order.setRestaurant(restaurant);

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {

            MenuItem menuItem =
                    menuItemRepository.findById(itemReq.getMenuItemId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Menu item not found"));

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setMenuItem(menuItem);
            oi.setQuantity(itemReq.getQuantity());
            oi.setPrice(menuItem.getPrice());

            order.getItems().add(oi);
            total = total.add(
                    menuItem.getPrice()
                            .multiply(BigDecimal.valueOf(itemReq.getQuantity()))
            );
        }

        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);

        return OrderMapper.toResponse(saved);

    }

    @Override
    public List<OrderResponse> getConsumerOrders(User user) {
        return orderRepository.findByConsumerId(user.getId())
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderStatus(Long orderId, User user) {
        Order order = orderRepository.findByIdAndConsumerId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        return OrderMapper.toResponse(order);
    }
}

