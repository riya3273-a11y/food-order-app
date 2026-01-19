package com.demo.foodorder.service.impl;

import com.demo.foodorder.dto.auth.LoginResponse;
import com.demo.foodorder.dto.auth.RegisterResponse;
import com.demo.foodorder.dto.auth.LoginRequest;
import com.demo.foodorder.dto.auth.RegisterRequest;
import com.demo.foodorder.entity.User;
import com.demo.foodorder.enums.Role;
import com.demo.foodorder.exception.BadRequestException;
import com.demo.foodorder.exception.ResourceNotFoundException;
import com.demo.foodorder.repository.UserRepository;
import com.demo.foodorder.security.JwtUtil;
import com.demo.foodorder.security.UserPrincipal;
import com.demo.foodorder.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Register new user
     * CONSUMER: auto-activated
     * RESTAURANT_OWNER: requires admin approval (inactive until restaurant created)
     */
    @Override
    public RegisterResponse register(RegisterRequest request) {

        Role role = validateAndGetRole(request.getRole());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(role)
                .active(role == Role.CONSUMER)  // CONSUMER active immediately, RESTAURANT_OWNER needs admin approval
                .build();

        User savedUser = userRepository.save(user);
        String successMessage = "Registration successful";
        
        return RegisterResponse.builder()
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().toString())
                .message(role == Role.CONSUMER 
                    ? successMessage 
                    : successMessage + " - Contact Admin to create restaurant account")
                .build();
    }

    /**
     * Login for all roles
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = getCurrentUser();
        
        if (!user.getActive()) {
            throw new BadRequestException("Account not active. Please contact admin for approval.");
        }
        
        String token = jwtUtil.generateToken(request.getUsername());
        
        return LoginResponse.builder()
                .tokenType("Bearer")
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().toString())
                .message("Login successful")
                .build();
    }

    private Role validateAndGetRole(String role) {
        try {
            Role parsedRole = Role.valueOf(role.toUpperCase());
            if (parsedRole == Role.ADMIN) {
                throw new BadRequestException("Admin registration is not allowed");
            }
            return parsedRole;

        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(
                    "Invalid role. Allowed values: CONSUMER, RESTAURANT_OWNER"
            );
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("No authenticated user");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUser();
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
