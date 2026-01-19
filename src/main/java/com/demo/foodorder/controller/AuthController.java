package com.demo.foodorder.controller;

import com.demo.foodorder.dto.ApiResponse;
import com.demo.foodorder.dto.auth.LoginResponse;
import com.demo.foodorder.dto.auth.RegisterResponse;
import com.demo.foodorder.dto.auth.LoginRequest;
import com.demo.foodorder.dto.auth.RegisterRequest;
import com.demo.foodorder.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "1. Authentication", description = "Apis tp register/login account")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Register new user",
            description = "Register as CONSUMER (auto-activated) or RESTAURANT_OWNER (requires admin approval once registered). No authentication required."
    )
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {
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
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // Since we use JWT stateless, logout is handled client-side by removing the token
        // This endpoint exists for formal API completeness
        return ResponseEntity.ok("Logout successful. Please remove the token from client.");
    }
}
