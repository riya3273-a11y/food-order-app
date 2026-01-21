package com.demo.foodorder.service;

import com.demo.foodorder.dto.response.FoodSearchResponse;

import java.util.List;

public interface FoodSuggestionService {
    List<FoodSearchResponse> getSuggestions(Long userId);
}
