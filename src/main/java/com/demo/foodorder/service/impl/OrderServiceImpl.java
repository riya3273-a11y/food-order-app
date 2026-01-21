package com.demo.foodorder.service.impl;

import com.demo.foodorder.dto.request.OrderItemRequest;
import com.demo.foodorder.dto.response.OrderResponse;
import com.demo.foodorder.dto.request.PlaceOrderRequest;
import com.demo.foodorder.entity.*;
import com.demo.foodorder.exception.BadRequestException;
import com.demo.foodorder.exception.DatabaseOperationException;
import com.demo.foodorder.exception.ResourceNotFoundException;
import com.demo.foodorder.exception.ServiceException;
import com.demo.foodorder.mapper.OrderMapper;
import com.demo.foodorder.repository.MenuItemRepository;
import com.demo.foodorder.repository.OrderRepository;
import com.demo.foodorder.repository.RestaurantRepository;
import com.demo.foodorder.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public OrderResponse placeOrder(
            PlaceOrderRequest request,
            User user) {
        try {
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

                // Verify menu item belongs to the restaurant and is available
                if (!menuItem.getRestaurant().getId().equals(restaurant.getId())) {
                    throw new BadRequestException("Menu item does not belong to this restaurant");
                }

                if (!menuItem.getAvailable() || !menuItem.getRestaurant().getActive()) {
                    throw new BadRequestException("Menu item '" + menuItem.getName() + "' is not available");
                }

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
        } catch (ResourceNotFoundException | BadRequestException e) {
            logger.error("Error placing order: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error placing order", e);
            throw new DatabaseOperationException("Error occurred during database operation", e);
        } catch (Exception e) {
            logger.error("Unexpected error placing order", e);
            throw new ServiceException("Error occurred during service operation", e);
        }
    }

    @Override
    public List<OrderResponse> getConsumerOrders(User user) {
        logger.info("Getting orders for consumer: {}", user.getId());
        return orderRepository.findByConsumerId(user.getId())
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderStatus(Long orderId, User user) {
        logger.info("Getting order status: orderId={}, userId={}", orderId, user.getId());
        Order order = orderRepository.findByIdAndConsumerId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return OrderMapper.toResponse(order);
    }
}

