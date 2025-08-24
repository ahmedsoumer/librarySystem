package com.assessment.librarySystem.service.interfaces;

import com.assessment.librarySystem.common.OperationResult;

import java.util.List;
import java.util.Optional;

/**
 * Generic CRUD service interface
 */
public interface CrudService<T, ID, CreateRequest, UpdateRequest, Response> {
    
    /**
     * Create a new entity
     */
    OperationResult<Response> create(CreateRequest request);
    
    /**
     * Find entity by ID
     */
    Optional<Response> findById(ID id);
    
    /**
     * Find all entities
     */
    List<Response> findAll();
    
    /**
     * Update existing entity
     */
    OperationResult<Response> update(ID id, UpdateRequest request);
    
    /**
     * Delete entity by ID
     */
    OperationResult<Void> delete(ID id);
    
    /**
     * Check if entity exists
     */
    boolean existsById(ID id);
}
