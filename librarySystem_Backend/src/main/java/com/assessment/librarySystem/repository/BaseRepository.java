package com.assessment.librarySystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

/**
 * Generic base repository interface with common operations
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
    
    /**
     * Find entity by ID with validation
     */
    default Optional<T> findByIdWithValidation(ID id) {
        if (id == null) {
            return Optional.empty();
        }
        return findById(id);
    }
    
    /**
     * Find all active entities (can be overridden by implementations)
     */
    default List<T> findAllActive() {
        return findAll();
    }
    
    /**
     * Check if entity exists by ID with null safety
     */
    default boolean existsByIdSafe(ID id) {
        return id != null && existsById(id);
    }
}
