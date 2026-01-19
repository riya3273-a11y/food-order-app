package com.demo.foodorder.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes for data.sql
 * Run this class to generate hashes for bootstrap users
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        
        System.out.println("==== Password Hashes for data.sql ====");
        System.out.println();
        
        System.out.println("Admin (password: admin123):");
        System.out.println(encoder.encode("admin123"));
        System.out.println();
        
        System.out.println("Restaurant Owners (password: owner123):");
        System.out.println(encoder.encode("owner123"));
        System.out.println();
        
        System.out.println("Consumers (password: consumer123):");
        System.out.println(encoder.encode("consumer123"));
        System.out.println();
    }
}
