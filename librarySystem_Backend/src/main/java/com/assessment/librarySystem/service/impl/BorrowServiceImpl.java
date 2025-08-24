package com.assessment.librarySystem.service.impl;

import com.assessment.librarySystem.model.BorrowRecord;
import com.assessment.librarySystem.repository.BorrowRecordRepository;
import com.assessment.librarySystem.service.interfaces.BookService;
import com.assessment.librarySystem.service.interfaces.BorrowService;
import com.assessment.librarySystem.service.interfaces.BorrowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of BorrowService interface.
 * Only includes methods needed for the 5 required endpoints.
 */
@Service
@Transactional
public class BorrowServiceImpl implements BorrowService {
    
    private final BorrowRecordRepository borrowRecordRepository;
    private final BorrowerService borrowerService;
    private final BookService bookService;
    
    @Autowired
    public BorrowServiceImpl(BorrowRecordRepository borrowRecordRepository,
                           BorrowerService borrowerService,
                           BookService bookService) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.borrowerService = borrowerService;
        this.bookService = bookService;
    }
    
    @Override
    public BorrowRecord borrowBook(Long borrowerId, Long bookId) {
        validateBorrowOperation(borrowerId, bookId);
        
        BorrowRecord borrowRecord = createBorrowRecord(borrowerId, bookId);
        return borrowRecordRepository.save(borrowRecord);
    }
    
    @Override
    public BorrowRecord returnBook(Long borrowerId, Long bookId) {
        validateReturnOperation(borrowerId, bookId);
        
        Optional<BorrowRecord> activeBorrowRecord = borrowRecordRepository
            .findByBorrowerIdAndBookIdAndReturnDateIsNull(borrowerId, bookId);
        
        if (activeBorrowRecord.isEmpty()) {
            throw new IllegalArgumentException("No active borrow record found for borrower " + 
                borrowerId + " and book " + bookId);
        }
        
        BorrowRecord record = activeBorrowRecord.get();
        record.setReturnDate(LocalDateTime.now());
        return borrowRecordRepository.save(record);
    }
    
    private void validateBorrowOperation(Long borrowerId, Long bookId) {
        if (borrowerId == null) {
            throw new IllegalArgumentException("Borrower ID cannot be null");
        }
        if (bookId == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }
        
        // Check if borrower exists
        if (borrowerService.getBorrowerById(borrowerId).isEmpty()) {
            throw new IllegalArgumentException("Borrower not found with ID: " + borrowerId);
        }
        
        // Check if book exists
        if (bookService.getBookById(bookId).isEmpty()) {
            throw new IllegalArgumentException("Book not found with ID: " + bookId);
        }
        
        // Check if book is available
        if (!isBookAvailable(bookId)) {
            throw new IllegalArgumentException("Book with ID " + bookId + " is already borrowed");
        }
    }
    
    private void validateReturnOperation(Long borrowerId, Long bookId) {
        if (borrowerId == null) {
            throw new IllegalArgumentException("Borrower ID cannot be null");
        }
        if (bookId == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }
        
        // Check if borrower exists
        if (borrowerService.getBorrowerById(borrowerId).isEmpty()) {
            throw new IllegalArgumentException("Borrower not found with ID: " + borrowerId);
        }
        
        // Check if book exists
        if (bookService.getBookById(bookId).isEmpty()) {
            throw new IllegalArgumentException("Book not found with ID: " + bookId);
        }
        
        // Check if there's an active borrow record
        Optional<BorrowRecord> activeBorrowRecord = borrowRecordRepository
            .findByBorrowerIdAndBookIdAndReturnDateIsNull(borrowerId, bookId);
        
        if (activeBorrowRecord.isEmpty()) {
            throw new IllegalArgumentException("No active borrow record found for borrower " + 
                borrowerId + " and book " + bookId);
        }
    }
    
    private boolean isBookAvailable(Long bookId) {
        if (bookId == null) {
            return false;
        }
        return borrowRecordRepository.findByBookIdAndReturnDateIsNull(bookId).isEmpty();
    }
    
    private BorrowRecord createBorrowRecord(Long borrowerId, Long bookId) {
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setBorrower(borrowerService.getBorrowerById(borrowerId).orElse(null));
        borrowRecord.setBook(bookService.getBookById(bookId).orElse(null));
        borrowRecord.setBorrowDate(LocalDateTime.now());
        return borrowRecord;
    }
}
