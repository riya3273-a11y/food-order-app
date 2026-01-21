package com.demo.foodorder.security;

import com.demo.foodorder.entity.User;
import com.demo.foodorder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsProvider implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found or account inactive"));

        return new UserPrincipal(user);
    }
}
