package com.demo.foodorder.controller;

import com.demo.foodorder.dto.response.FoodSearchResponse;
import com.demo.foodorder.security.UserPrincipal;
import com.demo.foodorder.service.FoodSuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/suggestions")
@RequiredArgsConstructor
@Tag(name = "7. Food Suggestions", description = "Requires CONSUMER role")
@SecurityRequirement(name = "bearerAuth")
public class FoodSuggestionController {

    private static final Logger logger = LoggerFactory.getLogger(FoodSuggestionController.class);
    private final FoodSuggestionService suggestionService;

    @Operation(summary = "Get personalized food suggestions based on past orders")
    @GetMapping
    public ResponseEntity<List<FoodSearchResponse>> getSuggestions(
            @AuthenticationPrincipal UserPrincipal principal) {
        logger.info("Getting food suggestions for user: {}", principal.getId());
        return ResponseEntity.ok(
                suggestionService.getSuggestions(principal.getId())
        );
    }
}
