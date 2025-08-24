package com.assessment.librarySystem.mapper;

import com.assessment.librarySystem.dto.response.BorrowOperationResponse;
import com.assessment.librarySystem.model.BorrowRecord;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BorrowRecord entities and DTOs.
 * Only includes methods needed for the 5 required endpoints.
 */
@Component
public class BorrowRecordMapper {
    
    /**
     * Convert BorrowRecord entity to BorrowOperationResponse DTO for borrow operation
     */
    public BorrowOperationResponse toBorrowOperationResponse(BorrowRecord borrowRecord) {
        return new BorrowOperationResponse(
            borrowRecord.getId(),
            borrowRecord.getBorrower().getId(),
            borrowRecord.getBorrower().getName(),
            borrowRecord.getBook().getId(),
            borrowRecord.getBook().getTitle(),
            borrowRecord.getBook().getAuthor(),
            borrowRecord.getBorrowDate(),
            "BORROWED",
            String.format("Book '%s' has been successfully borrowed by %s", 
                borrowRecord.getBook().getTitle(), 
                borrowRecord.getBorrower().getName())
        );
    }
    
    /**
     * Convert BorrowRecord entity to BorrowOperationResponse DTO for return operation
     */
    public BorrowOperationResponse toReturnOperationResponse(BorrowRecord borrowRecord) {
        return new BorrowOperationResponse(
            borrowRecord.getId(),
            borrowRecord.getBorrower().getId(),
            borrowRecord.getBorrower().getName(),
            borrowRecord.getBook().getId(),
            borrowRecord.getBook().getTitle(),
            borrowRecord.getBook().getAuthor(),
            borrowRecord.getReturnDate(),
            "RETURNED",
            String.format("Book '%s' has been successfully returned by %s", 
                borrowRecord.getBook().getTitle(), 
                borrowRecord.getBorrower().getName())
        );
    }
}
