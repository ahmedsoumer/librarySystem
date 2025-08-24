package com.assessment.librarySystem.dto.response;

/**
 * Response DTO for borrower information
 */
public record BorrowerResponse(
    Long id,
    String name,
    String email
) {}
