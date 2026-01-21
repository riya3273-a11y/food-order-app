package com.demo.foodorder.service.impl;

import com.demo.foodorder.dto.request.CreateRestaurantRequest;
import com.demo.foodorder.dto.request.UpdateRestaurantRequest;
import com.demo.foodorder.dto.response.RestaurantResponse;
import com.demo.foodorder.entity.Restaurant;
import com.demo.foodorder.entity.User;
import com.demo.foodorder.enums.Role;
import com.demo.foodorder.exception.BadRequestException;
import com.demo.foodorder.exception.DatabaseOperationException;
import com.demo.foodorder.exception.ResourceNotFoundException;
import com.demo.foodorder.exception.ServiceException;
import com.demo.foodorder.mapper.RestaurantMapper;
import com.demo.foodorder.repository.RestaurantRepository;
import com.demo.foodorder.repository.UserRepository;
import com.demo.foodorder.service.AdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    @Transactional
    public RestaurantResponse createRestaurant(CreateRestaurantRequest request) {
        try {
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
        } catch (ResourceNotFoundException | BadRequestException e) {
            logger.error("Error creating restaurant: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error creating restaurant", e);
            throw new DatabaseOperationException("Error occurred during database operation", e);
        } catch (Exception e) {
            logger.error("Unexpected error creating restaurant", e);
            throw new ServiceException("Error occurred during service operation", e);
        }
    }

    @Transactional
    public RestaurantResponse updateRestaurant(
            Long restaurantId,
            UpdateRestaurantRequest request) {
        try {
            Restaurant restaurant = restaurantRepository.findByIdAndActiveTrue(restaurantId)
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
        } catch (ResourceNotFoundException | BadRequestException e) {
            logger.error("Error updating restaurant: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error updating restaurant", e);
            throw new DatabaseOperationException("Error occurred during database operation", e);
        } catch (Exception e) {
            logger.error("Unexpected error updating restaurant", e);
            throw new ServiceException("Error occurred during service operation", e);
        }
    }

    @Transactional
    public void deleteRestaurant(Long restaurantId) {
        try {
            Restaurant restaurant = restaurantRepository.findByIdAndActiveTrue(restaurantId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Restaurant not found"));

            restaurant.setActive(false);
            restaurantRepository.save(restaurant);

            restaurant.getMenuItems().forEach(menuItem -> {
                menuItem.setAvailable(false);
            });

            User owner = restaurant.getOwner();
            long remainingRestaurants =
                    restaurantRepository.countByOwnerIdAndActiveTrue(owner.getId());

            if (remainingRestaurants == 0) {
                owner.setActive(false);
                userRepository.save(owner);
            }
        } catch (ResourceNotFoundException | BadRequestException e) {
            logger.error("Error deleting restaurant: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error deleting restaurant", e);
            throw new DatabaseOperationException("Error occurred during database operation", e);
        } catch (Exception e) {
            logger.error("Unexpected error deleting restaurant", e);
            throw new ServiceException("Error occurred during service operation", e);
        }
    }

}
