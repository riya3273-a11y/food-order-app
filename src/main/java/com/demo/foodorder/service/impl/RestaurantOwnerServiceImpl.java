package com.demo.foodorder.service.impl;

import com.demo.foodorder.dto.request.MenuItemRequest;
import com.demo.foodorder.dto.request.TimingRequest;
import com.demo.foodorder.dto.response.MenuItemResponse;
import com.demo.foodorder.dto.response.OrderResponse;
import com.demo.foodorder.dto.response.RestaurantResponse;
import com.demo.foodorder.dto.response.TimingResponse;
import com.demo.foodorder.entity.MenuItem;
import com.demo.foodorder.entity.Order;
import com.demo.foodorder.entity.Restaurant;
import com.demo.foodorder.entity.RestaurantTiming;
import com.demo.foodorder.enums.OrderStatus;
import com.demo.foodorder.exception.BadRequestException;
import com.demo.foodorder.exception.DatabaseOperationException;
import com.demo.foodorder.exception.ResourceNotFoundException;
import com.demo.foodorder.exception.ServiceException;
import com.demo.foodorder.mapper.MenuItemMapper;
import com.demo.foodorder.mapper.OrderMapper;
import com.demo.foodorder.mapper.RestaurantMapper;
import com.demo.foodorder.mapper.RestaurantTimingMapper;
import com.demo.foodorder.repository.MenuItemRepository;
import com.demo.foodorder.repository.OrderRepository;
import com.demo.foodorder.repository.RestaurantRepository;
import com.demo.foodorder.repository.RestaurantTimingRepository;
import com.demo.foodorder.service.RestaurantOwnerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantOwnerServiceImpl implements RestaurantOwnerService {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantOwnerServiceImpl.class);
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantTimingRepository timingRepository;
    private final OrderRepository orderRepository;

    private Restaurant loadActiveRestaurantForOwner(Long restaurantId, Long ownerId) {
        return restaurantRepository.findByIdAndOwnerIdAndActiveTrue(restaurantId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found or inactive"));
    }

    @Override
    public List<RestaurantResponse> getOwnerRestaurants(Long ownerId) {
        return restaurantRepository.findByOwnerIdAndActiveTrue(ownerId).stream()
                .map(RestaurantMapper::toResponse)
                .toList();
    }


    @Transactional
    @Override
    public MenuItemResponse addMenuItem(Long restaurantId, MenuItemRequest req, Long ownerId) {
        try {
            Restaurant restaurant = loadActiveRestaurantForOwner(restaurantId, ownerId);
            if (menuItemRepository.existsByRestaurantIdAndNameIgnoreCaseAndAvailableTrue(restaurantId, req.getName())) {
                throw new BadRequestException("Menu item with the same name already exists for this restaurant");
            }
            MenuItem item = MenuItem.builder()
                    .restaurant(restaurant)
                    .name(req.getName())
                    .description(req.getDescription())
                    .foodCategory(req.getCategory())
                    .cuisineType(req.getCuisine())
                    .price(req.getPrice())
                    .available(req.getAvailable())
                    .vegetarian(req.getVegetarian())
                    .vegan(req.getVegan())
                    .glutenFree(req.getGlutenFree())
                    .build();
            MenuItem saved = menuItemRepository.save(item);
            return MenuItemMapper.toResponse(saved);
        } catch (ResourceNotFoundException | BadRequestException e) {
            logger.error("Error adding menu item: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error adding menu item", e);
            throw new DatabaseOperationException("Error occurred during database operation", e);
        } catch (Exception e) {
            logger.error("Unexpected error adding menu item", e);
            throw new ServiceException("Error occurred during service operation", e);
        }
    }

    @Transactional
    @Override
    public MenuItemResponse updateMenuItem(Long restaurantId, Long itemId, MenuItemRequest req, Long ownerId) {
        try {
            loadActiveRestaurantForOwner(restaurantId, ownerId);

            MenuItem item = menuItemRepository.findByIdAndRestaurantIdAndAvailableTrue(itemId, restaurantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found or unavailable"));

            if (req.getName() != null && !item.getName().equalsIgnoreCase(req.getName())
                    && menuItemRepository.existsByRestaurantIdAndNameIgnoreCaseAndAvailableTrue(restaurantId, req.getName())) {
                throw new BadRequestException("Menu item with the name to be updated already exists for this restaurant");
            }

            item.setName(req.getName() == null ? item.getName() : req.getName());
            item.setDescription(req.getDescription() == null ? item.getDescription() : req.getDescription());
            item.setFoodCategory(req.getCategory() == null ? item.getFoodCategory() : req.getCategory());
            item.setCuisineType(req.getCuisine() == null ? item.getCuisineType() : req.getCuisine());
            item.setPrice(req.getPrice() == null ? item.getPrice() : req.getPrice());
            item.setAvailable(req.getAvailable() == null ? item.getAvailable() : req.getAvailable());
            item.setVegetarian(req.getVegetarian() == null ? item.getVegetarian() : req.getVegetarian());
            item.setVegan(req.getVegan() == null ? item.getVegan() : req.getVegan());
            item.setGlutenFree(req.getGlutenFree() == null ? item.getGlutenFree() : req.getGlutenFree());

            MenuItem saved = menuItemRepository.save(item);
            return MenuItemMapper.toResponse(saved);
        } catch (ResourceNotFoundException | BadRequestException e) {
            logger.error("Error updating menu item: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error updating menu item", e);
            throw new DatabaseOperationException("Error occurred during database operation", e);
        } catch (Exception e) {
            logger.error("Unexpected error updating menu item", e);
            throw new ServiceException("Error occurred during service operation", e);
        }
    }

    @Transactional
    @Override
    public void deleteMenuItem(Long restaurantId, Long itemId, Long ownerId) {
        try {
            loadActiveRestaurantForOwner(restaurantId, ownerId);

            MenuItem item = menuItemRepository.findByIdAndRestaurantIdAndAvailableTrue(itemId, restaurantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found or unavailable"));

            item.setAvailable(false);
            menuItemRepository.save(item);
        } catch (ResourceNotFoundException | BadRequestException e) {
            logger.error("Error deleting menu item: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error deleting menu item", e);
            throw new DatabaseOperationException("Error occurred during database operation", e);
        } catch (Exception e) {
            logger.error("Unexpected error deleting menu item", e);
            throw new ServiceException("Error occurred during service operation", e);
        }
    }

    @Transactional
    @Override
    public List<TimingResponse> updateRestaurantTimings(
            Long restaurantId,
            List<TimingRequest> timings,
            Long ownerId) {
        try {
            if (timings == null || timings.isEmpty()) {
                throw new BadRequestException("Timings list cannot be empty");
            }

            Restaurant restaurant =
                    loadActiveRestaurantForOwner(restaurantId, ownerId);

            Set<DayOfWeek> seenDays = new HashSet<>();
            for (TimingRequest req : timings) {
                if (!seenDays.add(req.getDayOfWeek())) {
                    throw new BadRequestException(
                            "Duplicate day provided: " + req.getDayOfWeek()
                    );
                }
            }

            List<RestaurantTiming> existingTimings =
                    timingRepository.findByRestaurantId(restaurantId);

            Map<DayOfWeek, RestaurantTiming> existingMap = existingTimings.stream()
                    .collect(Collectors.toMap(
                            RestaurantTiming::getDayOfWeek,
                            timing -> timing
                    ));

            List<TimingResponse> responses = new ArrayList<>();
            Set<DayOfWeek> processedDays = new HashSet<>();

            for (TimingRequest req : timings) {
                LocalTime open;
                LocalTime close;
                try {
                    open = LocalTime.parse(req.getOpenTime());
                    close = LocalTime.parse(req.getCloseTime());
                } catch (Exception ex) {
                    throw new BadRequestException(
                            "Invalid time format for " + req.getDayOfWeek()
                    );
                }

                if (!open.isBefore(close)) {
                    throw new BadRequestException(
                            "openTime must be before closeTime for " + req.getDayOfWeek()
                    );
                }

                RestaurantTiming timing = existingMap.get(req.getDayOfWeek());

                if (timing != null) {
                    timing.setOpenTime(open);
                    timing.setCloseTime(close);
                } else {
                    timing = RestaurantTiming.builder()
                            .restaurant(restaurant)
                            .dayOfWeek(req.getDayOfWeek())
                            .openTime(open)
                            .closeTime(close)
                            .build();
                }

                RestaurantTiming saved = timingRepository.save(timing);
                processedDays.add(req.getDayOfWeek());

                responses.add(RestaurantTimingMapper.toResponse(saved));
            }

            List<RestaurantTiming> timingsToDelete = existingTimings.stream()
                    .filter(timing -> !processedDays.contains(timing.getDayOfWeek()))
                    .toList();

            if (!timingsToDelete.isEmpty()) {
                timingRepository.deleteAll(timingsToDelete);
            }

            return responses;
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Error occurred during database operation", e);
        } catch (Exception e) {
            throw new ServiceException("Error occurred during service operation", e);
        }
    }

    @Override
    public List<OrderResponse> getRestaurantOrders(Long restaurantId, OrderStatus status, Long ownerId) {
        loadActiveRestaurantForOwner(restaurantId, ownerId);

        List<Order> orders;
        if (status != null) {
            orders = orderRepository.findByRestaurantIdAndStatusAndRestaurantActiveTrue(restaurantId, status);
        } else {
            orders = orderRepository.findByRestaurantIdAndRestaurantActiveTrue(restaurantId);
        }

        return orders.stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Transactional
    @Override
    public OrderResponse approveOrder(Long restaurantId, Long orderId, Long ownerId) {
        try {
            loadActiveRestaurantForOwner(restaurantId, ownerId);

            Order order = orderRepository.findByIdAndRestaurantIdAndRestaurantActiveTrue(orderId, restaurantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found for this restaurant"));

            if (order.getStatus() != OrderStatus.PLACED) {
                throw new BadRequestException("Only PLACED orders can be approved");
            }

            return updateOrderStatusInternal(order, OrderStatus.APPROVED);
        } catch (ResourceNotFoundException | BadRequestException e) {
            logger.error("Error approving order: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error approving order", e);
            throw new DatabaseOperationException("Error occurred during database operation", e);
        } catch (Exception e) {
            logger.error("Unexpected error approving order", e);
            throw new ServiceException("Error occurred during service operation", e);
        }
    }

    @Transactional
    @Override
    public OrderResponse updateOrderStatus(Long restaurantId, Long orderId, OrderStatus status, Long ownerId) {
        try {
            loadActiveRestaurantForOwner(restaurantId, ownerId);

            Order order = orderRepository.findByIdAndRestaurantIdAndRestaurantActiveTrue(orderId, restaurantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found for this restaurant"));

            validateStatusTransition(order.getStatus(), status);

            return updateOrderStatusInternal(order, status);
        } catch (ResourceNotFoundException | BadRequestException e) {
            logger.error("Error updating order status: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error updating order status", e);
            throw new DatabaseOperationException("Error occurred during database operation", e);
        } catch (Exception e) {
            logger.error("Unexpected error updating order status", e);
            throw new ServiceException("Error occurred during service operation", e);
        }
    }

    private OrderResponse updateOrderStatusInternal(Order order, OrderStatus newStatus) {
        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);
        return OrderMapper.toResponse(updated);
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        boolean valid = switch (currentStatus) {
            case PLACED -> newStatus == OrderStatus.APPROVED || newStatus == OrderStatus.REJECTED;
            case APPROVED -> newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.CANCELLED;
            case PREPARING -> newStatus == OrderStatus.READY_FOR_PICKUP || newStatus == OrderStatus.CANCELLED;
            case READY_FOR_PICKUP -> newStatus == OrderStatus.COMPLETED || newStatus == OrderStatus.CANCELLED;
            default -> false;
        };
        if (!valid) {
            throw new BadRequestException(String.format("Invalid status transition from %s to %s", currentStatus, newStatus));
        }
    }
}
