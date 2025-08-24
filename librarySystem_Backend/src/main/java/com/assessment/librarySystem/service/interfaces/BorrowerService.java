package com.assessment.librarySystem.service.interfaces;

import com.assessment.librarySystem.model.Borrower;

import java.util.Optional;

/**
 * Service interface for borrower management operations.
 * Only includes methods needed for the 5 required endpoints.
 */
public interface BorrowerService {
    
    /**
     * Register a new borrower in the system
     * @param borrower The borrower to register
     * @return The registered borrower with assigned ID
     * @throws IllegalArgumentException if borrower data is invalid
     */
    Borrower registerBorrower(Borrower borrower);
    
    /**
     * Find a borrower by their unique ID (needed for borrow/return operations)
     * @param id The borrower's ID
     * @return Optional containing the borrower if found
     */
    Optional<Borrower> getBorrowerById(Long id);
}
