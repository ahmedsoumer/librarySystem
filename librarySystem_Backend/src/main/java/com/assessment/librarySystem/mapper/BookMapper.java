package com.assessment.librarySystem.mapper;

import com.assessment.librarySystem.dto.request.BookRegistrationRequest;
import com.assessment.librarySystem.dto.response.BookResponse;
import com.assessment.librarySystem.model.Book;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for converting between Book entities and DTOs.
 * Only includes methods needed for the 5 required endpoints.
 */
@Component
public class BookMapper implements EntityMapper<Book, BookResponse, BookRegistrationRequest> {
    
    /**
     * Convert BookRegistrationRequest to Book entity
     */
    public Book toEntity(BookRegistrationRequest request) {
        return new Book(request.isbn(), request.title(), request.author());
    }
    
    /**
     * Convert Book entity to BookResponse DTO
     */
    public BookResponse toResponse(Book book) {
        return new BookResponse(
            book.getId(),
            book.getIsbn(),
            book.getTitle(),
            book.getAuthor(),
            true // Simplified - not checking availability for GET /books endpoint
        );
    }
    
    /**
     * Convert list of Book entities to list of BookResponse DTOs
     */
    public List<BookResponse> toResponseList(List<Book> books) {
        return books.stream()
                .map(this::toResponse)
                .toList();
    }
}
