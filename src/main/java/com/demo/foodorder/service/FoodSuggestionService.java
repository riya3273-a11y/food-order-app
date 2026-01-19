package com.demo.foodorder.service;

import com.demo.foodorder.dto.search.FoodSearchResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FoodSuggestionService {
    List<FoodSearchResponse> getSuggestions(Long userId);
}
