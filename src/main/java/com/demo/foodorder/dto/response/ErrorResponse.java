package com.demo.foodorder.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse<T> {

    private String message;
    private T error;
    private LocalDateTime timestamp;

    public static <T> ErrorResponse<T> error(String message) {
        return ErrorResponse.<T>builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ErrorResponse<T> error(String message, T error) {
        return ErrorResponse.<T>builder()
                .message(message)
                .error(error)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
