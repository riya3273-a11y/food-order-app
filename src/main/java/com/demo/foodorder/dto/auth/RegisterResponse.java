package com.demo.foodorder.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RegisterResponse {
    private String username;
    private String email;
    private String role;
    private String message;
}
