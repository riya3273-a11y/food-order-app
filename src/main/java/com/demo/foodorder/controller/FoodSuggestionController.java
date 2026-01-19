package com.demo.foodorder.controller;

import com.demo.foodorder.dto.search.FoodSearchResponse;
import com.demo.foodorder.security.UserPrincipal;
import com.demo.foodorder.service.FoodSuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/suggestions")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "7. Consumer - Suggestions", description = "Requires CONSUMER role")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
public class FoodSuggestionController {

    private final FoodSuggestionService suggestionService;

    @Operation(summary = "Get personalized food suggestions based on past orders")
    @GetMapping
    public ResponseEntity<List<FoodSearchResponse>> getSuggestions(
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                suggestionService.getSuggestions(principal.getId())
        );
    }
}
