package com.demo.foodorder.service.impl;

import com.demo.foodorder.dto.search.RestaurantBrowseResponse;
import com.demo.foodorder.repository.RestaurantRepository;
import com.demo.foodorder.repository.RestaurantTimingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantBrowseServiceImpl implements com.demo.foodorder.service.RestaurantBrowseService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTimingRepository timingRepository;

    @Override
    public List<RestaurantBrowseResponse> browseRestaurants(
            double userLat,
            double userLng,
            double radiusKm,
            boolean openNowOnly) {

        DayOfWeek today = DayOfWeek.valueOf(
                LocalDate.now().getDayOfWeek().name()
        );
        LocalTime now = LocalTime.now();

        return restaurantRepository.findByActiveTrue()
                .stream()
                .filter(r ->
                        distanceKm(
                                userLat, userLng,
                                r.getLatitude(), r.getLongitude()
                        ) <= radiusKm
                )
                // open-now filter
                .map(r -> {
                    boolean openNow = isOpenNow(r.getId(), today, now);
                    return new RestaurantBrowseResponse(
                            r.getId(),
                            r.getName(),
                            r.getAddress(),
                            r.getLatitude(),
                            r.getLongitude(),
                            openNow
                    );
                })
                .filter(r -> !openNowOnly || r.isOpenNow())
                .toList();
    }

    private boolean isOpenNow(Long restaurantId,
                              DayOfWeek today,
                              LocalTime now) {

        return timingRepository
                .findByRestaurantIdAndDayOfWeek(restaurantId, today)
                .map(t ->
                        !now.isBefore(t.getOpenTime())
                                && !now.isAfter(t.getCloseTime())
                )
                .orElse(false);
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
