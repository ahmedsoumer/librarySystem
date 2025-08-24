package com.assessment.librarySystem.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO for borrow/return operations
 */
public record BorrowOperationResponse(
    Long borrowRecordId,
    Long borrowerId,
    String borrowerName,
    Long bookId,
    String bookTitle,
    String bookAuthor,
    LocalDateTime operationDate,
    String operation, // "BORROWED" or "RETURNED"
    String message
) {}
