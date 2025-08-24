package com.assessment.librarySystem.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Enhanced error response record with generic details support
 */
public record ErrorResponse<T>(
    int status,
    String message,
    T details,
    LocalDateTime timestamp
) {
    public static ErrorResponse<Void> simple(int status, String message) {
        return new ErrorResponse<>(status, message, null, LocalDateTime.now());
    }
    
    public static <T> ErrorResponse<T> withDetails(int status, String message, T details) {
        return new ErrorResponse<>(status, message, details, LocalDateTime.now());
    }
    
    public static ErrorResponse<Map<String, Object>> withErrors(int status, String message, Map<String, Object> errors) {
        return new ErrorResponse<>(status, message, errors, LocalDateTime.now());
    }
}
