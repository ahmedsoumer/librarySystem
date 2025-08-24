package com.assessment.librarySystem.service.interfaces;

import com.assessment.librarySystem.model.Book;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for book management operations.
 * Only includes methods needed for the 5 required endpoints.
 */
public interface BookService {
    
    /**
     * Register a new book in the system
     * @param book The book to register
     * @return The registered book with assigned ID
     * @throws IllegalArgumentException if book data is invalid
     */
    Book registerBook(Book book);
    
    /**
     * Retrieve all books from the system
     * @return List of all registered books
     */
    List<Book> getAllBooks();
    
    /**
     * Find a book by its unique ID (needed for borrow/return operations)
     * @param id The book's ID
     * @return Optional containing the book if found
     */
    Optional<Book> getBookById(Long id);
}
