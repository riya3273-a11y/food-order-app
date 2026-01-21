package com.demo.foodorder.service.impl;

import com.demo.foodorder.dto.response.MenuItemResponse;
import com.demo.foodorder.dto.response.TimingResponse;
import com.demo.foodorder.dto.response.RestaurantResponse;
import com.demo.foodorder.entity.RestaurantTiming;
import com.demo.foodorder.exception.ResourceNotFoundException;
import com.demo.foodorder.mapper.MenuItemMapper;
import com.demo.foodorder.mapper.RestaurantMapper;
import com.demo.foodorder.mapper.RestaurantTimingMapper;
import com.demo.foodorder.repository.MenuItemRepository;
import com.demo.foodorder.repository.RestaurantRepository;
import com.demo.foodorder.repository.RestaurantTimingRepository;
import  com.demo.foodorder.entity.Restaurant;
import com.demo.foodorder.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantServiceImpl.class);
    private final RestaurantRepository restaurantRepository;
    private final RestaurantTimingRepository timingRepository;
    private  final MenuItemRepository menuItemRepository;

    @Override
    public List<RestaurantResponse> browseAllRestaurants(boolean openNowOnly) {
        return restaurantRepository.findByActiveTrue()
                .stream()
                .map(this::toRestaurantDetailedResponse)
                .filter(r -> !openNowOnly || ((r != null && r.getOpenNow())))
                .toList();
    }

    @Override
    public List<RestaurantResponse> browseRestaurants(
            double userLat,
            double userLng,
            double radiusKm,
            boolean openNow) {

        return restaurantRepository.findByActiveTrue()
                .stream()
                .filter(r ->
                        distanceKm(
                                userLat, userLng,
                                r.getLatitude(), r.getLongitude()
                        ) <= radiusKm
                )
                .map(this::toRestaurantDetailedResponse)
                .filter(r -> !openNow || (r != null && r.getOpenNow()))
                .toList();
    }

    public RestaurantResponse getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        DayOfWeek today = DayOfWeek.valueOf(LocalDate.now().getDayOfWeek().name());
        LocalTime now = LocalTime.now();
        var restaurantTiming = timingRepository
                .findByRestaurantIdAndDayOfWeek(restaurant.getId(), today)
                .orElse(null);
        if (restaurantTiming == null) return  RestaurantMapper.toResponse(restaurant);
        boolean openNow = isOpenNow(now, restaurantTiming);
        return RestaurantMapper.toResponse(restaurant, restaurantTiming, openNow);
    }

    @Override
    public List<MenuItemResponse> listMenu(Long restaurantId) {
        return menuItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId).stream()
                .map(MenuItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TimingResponse> getRestaurantTimings(Long restaurantId) {
        restaurantRepository.findByIdAndActiveTrue(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        return timingRepository.findByRestaurantId(restaurantId).stream()
                .map(RestaurantTimingMapper::toResponse)
                .toList();
    }

    private RestaurantResponse toRestaurantDetailedResponse(
            Restaurant restaurant) {

        DayOfWeek today = DayOfWeek.valueOf(LocalDate.now().getDayOfWeek().name());
        LocalTime now = LocalTime.now();
        var restaurantTiming = timingRepository
                .findByRestaurantIdAndDayOfWeek(restaurant.getId(), today)
                .orElse(null);
        boolean openNow = isOpenNow(now, restaurantTiming);
        return RestaurantMapper.toResponse(restaurant, restaurantTiming, openNow);
    }

    private static boolean isOpenNow(LocalTime now, RestaurantTiming timing) {
        return timing != null
                && !now.isBefore(timing.getOpenTime())
                && !now.isAfter(timing.getCloseTime());
    }

    /**
     * Haversine formula (km)
     */
    private double distanceKm(
            double lat1, double lon1,
            double lat2, double lon2) {

        final double R = 6371; // Earth radius km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
