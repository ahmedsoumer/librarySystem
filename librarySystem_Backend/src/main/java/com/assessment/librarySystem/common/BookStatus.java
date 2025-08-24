package com.assessment.librarySystem.common;

import java.time.LocalDateTime;

/**
 * Sealed interface representing different states a book can be in
 */
public sealed interface BookStatus 
    permits BookStatus.Available, BookStatus.Borrowed, BookStatus.Reserved {
    
    /**
     * Book is available for borrowing
     */
    record Available() implements BookStatus {}
    
    /**
     * Book is currently borrowed
     */
    record Borrowed(Long borrowerId, String borrowerName, LocalDateTime borrowDate) implements BookStatus {}
    
    /**
     * Book is reserved for a specific borrower
     */
    record Reserved(Long borrowerId, String borrowerName, LocalDateTime reservationDate) implements BookStatus {}
    
    /**
     * Check if book is available
     */
    default boolean isAvailable() {
        return this instanceof Available;
    }
    
    /**
     * Check if book is borrowed
     */
    default boolean isBorrowed() {
        return this instanceof Borrowed;
    }
    
    /**
     * Check if book is reserved
     */
    default boolean isReserved() {
        return this instanceof Reserved;
    }
    
    /**
     * Get the borrower ID if book is borrowed or reserved
     */
    default Long getBorrowerId() {
        if (this instanceof Borrowed borrowed) {
            return borrowed.borrowerId();
        } else if (this instanceof Reserved reserved) {
            return reserved.borrowerId();
        }
        return null;
    }
}
