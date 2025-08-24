package com.assessment.librarySystem.service.impl;

import com.assessment.librarySystem.model.Book;
import com.assessment.librarySystem.repository.BookRepository;
import com.assessment.librarySystem.service.interfaces.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of BookService interface.
 * Only includes methods needed for the 5 required endpoints.
 */
@Service
@Transactional
public class BookServiceImpl implements BookService {
    
    private final BookRepository bookRepository;
    
    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    @Override
    public Book registerBook(Book book) {
        validateBook(book);
        validateIsbnConsistency(book);
        return bookRepository.save(book);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Book> getBookById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }
        return bookRepository.findById(id);
    }
    
    private void validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title is required");
        }
        
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Book author is required");
        }
        
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new IllegalArgumentException("Book ISBN is required");
        }
        
        if (!isValidIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("Invalid ISBN format");
        }
    }
    
    private void validateIsbnConsistency(Book book) {
        List<Book> existingBooks = bookRepository.findByIsbn(book.getIsbn());
        if (!existingBooks.isEmpty()) {
            Book existingBook = existingBooks.get(0);
            if (!existingBook.getTitle().equals(book.getTitle()) || 
                !existingBook.getAuthor().equals(book.getAuthor())) {
                throw new IllegalArgumentException(
                    "Books with the same ISBN must have the same title and author. " +
                    "Existing: " + existingBook.getTitle() + " by " + existingBook.getAuthor() +
                    ", New: " + book.getTitle() + " by " + book.getAuthor()
                );
            }
        }
    }
    
    private boolean isValidIsbn(String isbn) {
        String cleanIsbn = isbn.replaceAll("[\\s-]", "");
        return cleanIsbn.matches("^\\d{10}$") || cleanIsbn.matches("^\\d{13}$");
    }
}
