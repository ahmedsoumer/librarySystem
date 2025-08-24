package com.assessment.librarySystem.mapper;

import java.util.List;

/**
 * Generic entity mapper interface for converting between entities, requests, and responses
 */
public interface EntityMapper<E, R, C> {
    
    /**
     * Convert create request to entity
     */
    E toEntity(C createRequest);
    
    /**
     * Convert entity to response DTO
     */
    R toResponse(E entity);
    
    /**
     * Convert list of entities to list of response DTOs
     */
    List<R> toResponseList(List<E> entities);
}
