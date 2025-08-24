package com.assessment.librarySystem.service.impl;

import com.assessment.librarySystem.model.Borrower;
import com.assessment.librarySystem.repository.BorrowerRepository;
import com.assessment.librarySystem.service.interfaces.BorrowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of BorrowerService interface.
 * Only includes methods needed for the 5 required endpoints.
 */
@Service
@Transactional
public class BorrowerServiceImpl implements BorrowerService {
    
    private final BorrowerRepository borrowerRepository;
    
    @Autowired
    public BorrowerServiceImpl(BorrowerRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }
    
    @Override
    public Borrower registerBorrower(Borrower borrower) {
        validateBorrower(borrower);
        checkEmailUniqueness(borrower.getEmail());
        return borrowerRepository.save(borrower);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Borrower> getBorrowerById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Borrower ID cannot be null");
        }
        return borrowerRepository.findById(id);
    }
    
    private void validateBorrower(Borrower borrower) {
        if (borrower == null) {
            throw new IllegalArgumentException("Borrower cannot be null");
        }
        
        if (borrower.getName() == null || borrower.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Borrower name is required");
        }
        
        if (borrower.getName().length() < 2 || borrower.getName().length() > 100) {
            throw new IllegalArgumentException("Borrower name must be between 2 and 100 characters");
        }
        
        if (borrower.getEmail() == null || borrower.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Borrower email is required");
        }
        
        if (!isValidEmail(borrower.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
    
    private void checkEmailUniqueness(String email) {
        if (borrowerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }
}
