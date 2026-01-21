package com.demo.foodorder.controller;

import com.demo.foodorder.dto.response.LoginResponse;
import com.demo.foodorder.dto.response.RegisterResponse;
import com.demo.foodorder.dto.request.LoginRequest;
import com.demo.foodorder.dto.request.RegisterRequest;
import com.demo.foodorder.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "1. Authentication", description = "Apis to register/login account")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @Operation(
            summary = "Register new user",
            description = "Register as role: CONSUMER (auto-activated) or RESTAURANT_OWNER (activated during restaurant creation). "
    )
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        logger.info("Registering new user: {}", request.getUsername());
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "User login",
            description = "Login with username and password to get JWT token. Use this token for authenticated endpoints."
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        logger.info("Login attempt for user: {}", request.getUsername());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

}
