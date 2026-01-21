package com.demo.foodorder.service;

import com.demo.foodorder.dto.request.LoginRequest;
import com.demo.foodorder.dto.request.RegisterRequest;
import com.demo.foodorder.dto.response.LoginResponse;
import com.demo.foodorder.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
