package com.demo.foodorder.service.impl;

import com.demo.foodorder.dto.menu.MenuItemRequest;
import com.demo.foodorder.dto.menu.MenuItemResponse;
import com.demo.foodorder.dto.menu.TimingRequest;
import com.demo.foodorder.dto.menu.TimingResponse;
import com.demo.foodorder.dto.order.OrderResponse;
import com.demo.foodorder.entity.*;
import com.demo.foodorder.enums.OrderStatus;
import com.demo.foodorder.exception.BadRequestException;
import com.demo.foodorder.exception.ResourceNotFoundException;
import com.demo.foodorder.mapper.OrderMapper;
import com.demo.foodorder.repository.*;
import com.demo.foodorder.service.RestaurantOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantOwnerServiceImpl implements RestaurantOwnerService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantTimingRepository timingRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public Restaurant loadActiveRestaurantForOwner(Long restaurantId, Long ownerId) {
        return restaurantRepository.findByIdAndOwnerIdAndActiveTrue(restaurantId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found or inactive"));
    }
    // Menu operations
    @Transactional
    @Override
    public MenuItemResponse addMenuItem(Long restaurantId, MenuItemRequest req, Long ownerId) {
        Restaurant restaurant = loadActiveRestaurantForOwner(restaurantId, ownerId);
        if (menuItemRepository.existsByRestaurantIdAndNameIgnoreCase(restaurantId, req.getName())) {
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
                .popularity(0L)
                .build();
        try {
            MenuItem saved = menuItemRepository.save(item);
            return toMenuItemResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Could not create menu item — possible invalid data");
        }
    }

    @Transactional
    @Override
    public MenuItemResponse updateMenuItem(Long restaurantId, Long itemId, MenuItemRequest req, Long ownerId) {
        loadActiveRestaurantForOwner(restaurantId, ownerId);

        MenuItem item = menuItemRepository.findByIdAndRestaurantId(itemId, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        String newName = req.getName().trim();
        if (!item.getName().equalsIgnoreCase(newName)
                && menuItemRepository.existsByRestaurantIdAndNameIgnoreCase(restaurantId, newName)) {
            throw new BadRequestException("Another menu item with the same name already exists");
        }

        item.setName(newName);
        item.setDescription(req.getDescription());
        item.setFoodCategory(req.getCategory());
        item.setCuisineType(req.getCuisine());
        item.setPrice(req.getPrice());
        item.setAvailable(req.getAvailable());
        item.setVegetarian(req.getVegetarian());
        item.setVegan(req.getVegan());
        item.setGlutenFree(req.getGlutenFree());

        try {
            MenuItem saved = menuItemRepository.save(item);
            return toMenuItemResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Could not update menu item — invalid data");
        }
    }

    @Transactional
    @Override
    public void deleteMenuItem(Long restaurantId, Long itemId, Long ownerId) {
        loadActiveRestaurantForOwner(restaurantId, ownerId);

        MenuItem item = menuItemRepository.findByIdAndRestaurantId(itemId, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        // soft-delete or hard delete? we'll hard delete for simplicity
        menuItemRepository.delete(item);
    }

    @Override
    public List<MenuItemResponse> listMenu(Long restaurantId, Long ownerId) {
        loadActiveRestaurantForOwner(restaurantId, ownerId);

        return menuItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId).stream()
                .map(this::toMenuItemResponse)
                .collect(Collectors.toList());
    }


    // Timing operations
    @Transactional
    public List<TimingResponse> replaceRestaurantTimings(
            Long restaurantId,
            List<TimingRequest> timings,
            Long ownerId) {

        if (timings == null || timings.isEmpty()) {
            throw new BadRequestException("Timings list cannot be empty");
        }

        Restaurant restaurant =
                loadActiveRestaurantForOwner(restaurantId, ownerId);

        List<RestaurantTiming> existing =
                timingRepository.findByRestaurantId(restaurantId);
        if (!existing.isEmpty()) {
            timingRepository.deleteAll(existing);
        }

        Set<DayOfWeek> seenDays = new HashSet<>();
        List<TimingResponse> responses = new ArrayList<>();

        for (TimingRequest req : timings) {

            if (!seenDays.add(req.getDayOfWeek())) {
                throw new BadRequestException(
                        "Duplicate day provided: " + req.getDayOfWeek()
                );
            }

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

            RestaurantTiming timing = new RestaurantTiming();
            timing.setRestaurant(restaurant);
            timing.setDayOfWeek(req.getDayOfWeek());
            timing.setOpenTime(open);
            timing.setCloseTime(close);

            RestaurantTiming saved = timingRepository.save(timing);

           responses.add(TimingResponse.builder().id(saved.getId()).dayOfWeek(saved.getDayOfWeek()).openTime(saved.getOpenTime()).closeTime(saved.getCloseTime()).build());

        }

        return responses;
    }


//    @Transactional
//    public List<TimingResponse> patchRestaurantTimings(
//            Long restaurantId,
//            List<TimingRequest> requests,
//            Long ownerId) {
//
//        Restaurant restaurant =
//                loadActiveRestaurantForOwner(restaurantId, ownerId);
//
//        // Prevent duplicate days in request
//        Set<DayOfWeek> seen = new HashSet<>();
//        for (TimingRequest req : requests) {
//            if (!seen.add(req.getDayOfWeek()) {
//                throw new BadRequestException(
//                        "Duplicate day in request: " + req.getDayOfWeek()
//                );
//            }
//        }
//
//        List<TimingResponse> responses = new ArrayList<>();
//
//        for (TimingRequest req : requests) {
//
//            LocalTime open = LocalTime.parse(req.getOpenTime());
//            LocalTime close = LocalTime.parse(req.getCloseTime());
//
//            if (!open.isBefore(close)) {
//                throw new BadRequestException(
//                        "openTime must be before closeTime for " + req.getDayOfWeek()
//                );
//            }
//
//            RestaurantTiming timing =
//                    timingRepository
//                            .findByRestaurantIdAndDayOfWeek(
//                                    restaurantId, req.getDayOfWeek()
//                            )
//                            .orElseGet(() -> {
//                                RestaurantTiming t = new RestaurantTiming();
//                                t.setRestaurant(restaurant);
//                                t.setDayOfWeek(req.getDayOfWeek());
//                                return t;
//                            });
//
//            timing.setOpenTime(open);
//            timing.setCloseTime(close);
//
//            RestaurantTiming saved = timingRepository.save(timing);
//
//            responses.add(
//                    TimingResponse.builder().dayOfWeek(saved.getDayOfWeek()).openTime(saved.getOpenTime()).closeTime(saved.getCloseTime()).build()
//            );
//        }
//
//        return responses;
//    }



    // Order management
    @Override
    public List<OrderResponse> getRestaurantOrders(Long restaurantId, Long ownerId) {
        loadActiveRestaurantForOwner(restaurantId, ownerId);
        
        return orderRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Transactional
    @Override
    public OrderResponse updateOrderStatus(Long restaurantId, Long orderId, OrderStatus status, Long ownerId) {
        loadActiveRestaurantForOwner(restaurantId, ownerId);
        
        Order order = orderRepository.findByIdAndRestaurantId(orderId, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for this restaurant"));
        
        // Validate status transition
        validateStatusTransition(order.getStatus(), status);
        
        order.setStatus(status);
        Order updated = orderRepository.save(order);
        
        return OrderMapper.toResponse(updated);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus newStatus) {
        // Business logic for valid status transitions
        if (current == OrderStatus.COMPLETED || current == OrderStatus.CANCELLED || current == OrderStatus.REJECTED) {
            throw new BadRequestException("Cannot update status of " + current + " order");
        }
        
        // Restaurant owner can approve, prepare, and mark ready for pickup
        if (newStatus == OrderStatus.PLACED) {
            throw new BadRequestException("Cannot change status back to PLACED");
        }
    }

    private MenuItemResponse toMenuItemResponse(MenuItem item) {
        return MenuItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .foodCategory(item.getFoodCategory())
                .cuisineType(item.getCuisineType())
                .price(item.getPrice())
                .available(item.getAvailable())
                .vegetarian(item.getVegetarian())
                .vegan(item.getVegan())
                .glutenFree(item.getGlutenFree())
                .build();
    }
}
