package com.assessment.librarySystem.repository;

import com.assessment.librarySystem.model.Borrower;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowerRepository extends BaseRepository<Borrower, Long> {
    
    Optional<Borrower> findByEmail(String email);
    
    boolean existsByEmail(String email);
}
