package com.demo.foodorder.repository;

import com.demo.foodorder.entity.User;
import com.demo.foodorder.enums.Role;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmailAndRole(String email, Role role);

    Optional<User> findByUsernameAndRole(String username, Role role);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void findByUsernameAndPassword(String username, String password);
}
