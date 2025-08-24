package com.assessment.librarySystem.dto.response;

/**
 * Response DTO for book information
 */
public record BookResponse(
    Long id,
    String isbn,
    String title,
    String author,
    boolean isAvailable
) {}
