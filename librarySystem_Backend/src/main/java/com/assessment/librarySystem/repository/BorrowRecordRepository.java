package com.assessment.librarySystem.repository;

import com.assessment.librarySystem.model.BorrowRecord;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends BaseRepository<BorrowRecord, Long> {
    
    /**
     * Find active borrow record by borrower and book IDs
     */
    Optional<BorrowRecord> findByBorrowerIdAndBookIdAndReturnDateIsNull(Long borrowerId, Long bookId);
    
    /**
     * Find active borrow record by book ID
     */
    Optional<BorrowRecord> findByBookIdAndReturnDateIsNull(Long bookId);
}
