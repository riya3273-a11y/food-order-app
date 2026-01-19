package com.demo.foodorder.service.impl;

import com.demo.foodorder.dto.restaurant.UpdateRestaurantRequest;
import com.demo.foodorder.enums.Role;
import com.demo.foodorder.exception.BadRequestException;
import com.demo.foodorder.mapper.RestaurantMapper;


import com.demo.foodorder.dto.restaurant.CreateRestaurantRequest;
import com.demo.foodorder.dto.restaurant.RestaurantResponse;
import com.demo.foodorder.entity.Restaurant;
import com.demo.foodorder.entity.User;
import com.demo.foodorder.exception.ResourceNotFoundException;
import com.demo.foodorder.repository.RestaurantRepository;
import com.demo.foodorder.repository.UserRepository;
import com.demo.foodorder.service.AdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    @Transactional
    public RestaurantResponse createRestaurant(CreateRestaurantRequest request) {

        User restaurantOwner = userRepository.findByEmailAndRole(request.getRestaurantOwnerEmail(), Role.RESTAURANT_OWNER)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant Owner not found"));

        if (restaurantRepository.existsByNameIgnoreCaseAndLatitudeAndLongitude(request.getName(), request.getLatitude(), request.getLongitude())) {
            throw new BadRequestException("Restaurant already exists at this location");
        }

        if (!restaurantOwner.getActive()) {
            restaurantOwner.setActive(true);
            restaurantOwner = userRepository.save(restaurantOwner);
        }

        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .address(request.getAddress())
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .owner(restaurantOwner)
                .active(true)
                .build();

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return RestaurantMapper.toResponse(savedRestaurant);
    }

    @Transactional
    public RestaurantResponse updateRestaurant(
            Long restaurantId,
            UpdateRestaurantRequest request){
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant not found"));

        String newName = (request.getName() == null || request.getName().isBlank())
                ? restaurant.getName()
                : request.getName();

        Double newLatitude = request.getLatitude() == null
                ? restaurant.getLatitude()
                : request.getLatitude();

        Double newLongitude = request.getLongitude() == null
                ? restaurant.getLongitude()
                : request.getLongitude();

        boolean isNameOrLocationChanged =
                !newName.equalsIgnoreCase(restaurant.getName())
                        || !newLatitude.equals(restaurant.getLatitude())
                        || !newLongitude.equals(restaurant.getLongitude());

        if (isNameOrLocationChanged &&
                restaurantRepository
                        .existsByNameIgnoreCaseAndLatitudeAndLongitude(
                                newName, newLatitude, newLongitude)) {

            throw new BadRequestException(
                    "Restaurant %s already exists at this location"
                            .formatted(newName));
        }

        restaurant.setName(newName);
        restaurant.setLatitude(newLatitude);
        restaurant.setLongitude(newLongitude);

        if (request.getAddress() != null) {
            restaurant.setAddress(request.getAddress());
        }

        if (request.getDescription() != null) {
            restaurant.setDescription(request.getDescription());
        }

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return RestaurantMapper.toResponse(updatedRestaurant);

    }

    @Transactional
    public void deleteRestaurant(Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant not found"));
        
        // Soft delete - mark restaurant as inactive
        restaurant.setActive(false);
        restaurantRepository.save(restaurant);
        
        // After saving, check if owner has any remaining active restaurants
        User owner = restaurant.getOwner();
        long remainingRestaurants =
                restaurantRepository.countByOwnerIdAndActiveTrue(owner.getId());

        // If no active restaurants remain, deactivate the owner account
        if (remainingRestaurants == 0) {
            owner.setActive(false);
            userRepository.save(owner);
        }
    }

}
