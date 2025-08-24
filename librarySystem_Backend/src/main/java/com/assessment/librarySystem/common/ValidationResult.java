package com.assessment.librarySystem.common;

import java.util.List;

/**
 * Sealed interface for validation results
 */
public sealed interface ValidationResult 
    permits ValidationResult.Valid, ValidationResult.Invalid {
    
    /**
     * Represents a valid validation result
     */
    record Valid() implements ValidationResult {}
    
    /**
     * Represents an invalid validation result with error details
     */
    record Invalid(List<String> errors) implements ValidationResult {}
    
    /**
     * Check if validation passed
     */
    default boolean isValid() {
        return this instanceof Valid;
    }
    
    /**
     * Check if validation failed
     */
    default boolean isInvalid() {
        return this instanceof Invalid;
    }
    
    /**
     * Get validation errors if invalid, empty list otherwise
     */
    default List<String> getErrors() {
        if (this instanceof Invalid invalid) {
            return invalid.errors();
        }
        return List.of();
    }
}
