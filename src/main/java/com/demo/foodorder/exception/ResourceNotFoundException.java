package com.demo.foodorder.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(ResourceNotFoundException e) {
        this(e.getMessage(), e.getCause());
    }
}
