package com.demo.foodorder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
    private String tokenType;
    private String token;
    private String username;
    private String role;
    private String message;
}

