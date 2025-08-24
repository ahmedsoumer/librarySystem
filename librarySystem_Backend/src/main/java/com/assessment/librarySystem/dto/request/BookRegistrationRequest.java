package com.assessment.librarySystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for book registration
 */
public record BookRegistrationRequest(
    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^978-\\d{10}$", message = "ISBN must follow format: 978-xxxxxxxxxx")
    String isbn,
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    String title,
    
    @NotBlank(message = "Author is required")
    @Size(min = 1, max = 255, message = "Author must be between 1 and 255 characters")
    String author
) {}
