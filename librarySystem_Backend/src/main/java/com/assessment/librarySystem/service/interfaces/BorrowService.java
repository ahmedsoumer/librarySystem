package com.assessment.librarySystem.service.interfaces;

import com.assessment.librarySystem.model.BorrowRecord;

/**
 * Service interface for borrow management operations.
 * Only includes methods needed for the 5 required endpoints.
 */
public interface BorrowService {
    
    /**
     * Borrow a book for a borrower
     * @param borrowerId The ID of the borrower
     * @param bookId The ID of the book to borrow
     * @return The created borrow record
     * @throws IllegalArgumentException if borrower or book doesn't exist, or book is already borrowed
     */
    BorrowRecord borrowBook(Long borrowerId, Long bookId);
    
    /**
     * Return a borrowed book
     * @param borrowerId The ID of the borrower
     * @param bookId The ID of the book to return
     * @return The updated borrow record
     * @throws IllegalArgumentException if no active borrow record exists
     */
    BorrowRecord returnBook(Long borrowerId, Long bookId);
}
