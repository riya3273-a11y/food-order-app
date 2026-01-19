package com.demo.foodorder.service;

import com.demo.foodorder.dto.auth.LoginResponse;
import com.demo.foodorder.dto.auth.RegisterResponse;
import com.demo.foodorder.dto.auth.LoginRequest;
import com.demo.foodorder.dto.auth.RegisterRequest;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
