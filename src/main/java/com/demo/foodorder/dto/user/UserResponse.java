package com.demo.foodorder.dto.user;

import com.demo.foodorder.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private Role role;
}
