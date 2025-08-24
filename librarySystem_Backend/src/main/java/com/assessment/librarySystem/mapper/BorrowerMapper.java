package com.assessment.librarySystem.mapper;

import com.assessment.librarySystem.dto.request.BorrowerRegistrationRequest;
import com.assessment.librarySystem.dto.response.BorrowerResponse;
import com.assessment.librarySystem.model.Borrower;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for converting between Borrower entities and DTOs
 */
@Component
public class BorrowerMapper implements EntityMapper<Borrower, BorrowerResponse, BorrowerRegistrationRequest> {
    
    /**
     * Convert BorrowerRegistrationRequest to Borrower entity
     */
    public Borrower toEntity(BorrowerRegistrationRequest request) {
        return new Borrower(request.name(), request.email());
    }
    
    /**
     * Convert Borrower entity to BorrowerResponse DTO
     */
    public BorrowerResponse toResponse(Borrower borrower) {
        return new BorrowerResponse(
            borrower.getId(),
            borrower.getName(),
            borrower.getEmail()
        );
    }
    
    /**
     * Convert list of Borrower entities to list of BorrowerResponse DTOs
     */
    public List<BorrowerResponse> toResponseList(List<Borrower> borrowers) {
        return borrowers.stream()
                .map(this::toResponse)
                .toList();
    }
}
