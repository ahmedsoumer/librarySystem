package com.assessment.librarySystem.service;

import com.assessment.librarySystem.model.Book;
import com.assessment.librarySystem.repository.BookRepository;
import com.assessment.librarySystem.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book validBook;

    @BeforeEach
    void setUp() {
        validBook = new Book("978-0134685991", "Effective Java", "Joshua Bloch");
        validBook.setId(1L);
    }

    @Test
    void registerBook_Success() {
        // Given
        Book newBook = new Book("978-0321356680", "Java: The Complete Reference", "Herbert Schildt");
        when(bookRepository.findByIsbn(anyString())).thenReturn(Collections.emptyList());
        when(bookRepository.save(any(Book.class))).thenReturn(validBook);

        // When
        Book result = bookService.registerBook(newBook);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookRepository).findByIsbn("978-0321356680");
        verify(bookRepository).save(newBook);
    }

    @Test
    void registerBook_NullBook_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookService.registerBook(null)
        );
        assertEquals("Book cannot be null", exception.getMessage());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void registerBook_EmptyTitle_ThrowsException() {
        // Given
        Book invalidBook = new Book("978-0134685991", "", "Joshua Bloch");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookService.registerBook(invalidBook)
        );
        assertEquals("Book title is required", exception.getMessage());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void registerBook_EmptyAuthor_ThrowsException() {
        // Given
        Book invalidBook = new Book("978-0134685991", "Effective Java", "");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookService.registerBook(invalidBook)
        );
        assertEquals("Book author is required", exception.getMessage());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void registerBook_EmptyIsbn_ThrowsException() {
        // Given
        Book invalidBook = new Book("", "Effective Java", "Joshua Bloch");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookService.registerBook(invalidBook)
        );
        assertEquals("Book ISBN is required", exception.getMessage());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void registerBook_InvalidIsbn_ThrowsException() {
        // Given
        Book invalidBook = new Book("invalid-isbn", "Effective Java", "Joshua Bloch");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookService.registerBook(invalidBook)
        );
        assertEquals("Invalid ISBN format", exception.getMessage());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void registerBook_SameIsbnSameTitleAuthor_Success() {
        // Given
        Book existingBook = new Book("978-0134685991", "Effective Java", "Joshua Bloch");
        Book newBook = new Book("978-0134685991", "Effective Java", "Joshua Bloch");
        
        when(bookRepository.findByIsbn("978-0134685991")).thenReturn(Arrays.asList(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(newBook);

        // When
        Book result = bookService.registerBook(newBook);

        // Then
        assertNotNull(result);
        verify(bookRepository).findByIsbn("978-0134685991");
        verify(bookRepository).save(newBook);
    }

    @Test
    void registerBook_SameIsbnDifferentTitle_ThrowsException() {
        // Given
        Book existingBook = new Book("978-0134685991", "Effective Java", "Joshua Bloch");
        Book newBook = new Book("978-0134685991", "Different Title", "Joshua Bloch");
        
        when(bookRepository.findByIsbn("978-0134685991")).thenReturn(Arrays.asList(existingBook));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookService.registerBook(newBook)
        );
        assertTrue(exception.getMessage().contains("Books with the same ISBN must have the same title and author"));
        verify(bookRepository).findByIsbn("978-0134685991");
        verify(bookRepository, never()).save(any());
    }

    @Test
    void registerBook_SameIsbnDifferentAuthor_ThrowsException() {
        // Given
        Book existingBook = new Book("978-0134685991", "Effective Java", "Joshua Bloch");
        Book newBook = new Book("978-0134685991", "Effective Java", "Different Author");
        
        when(bookRepository.findByIsbn("978-0134685991")).thenReturn(Arrays.asList(existingBook));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookService.registerBook(newBook)
        );
        assertTrue(exception.getMessage().contains("Books with the same ISBN must have the same title and author"));
        verify(bookRepository).findByIsbn("978-0134685991");
        verify(bookRepository, never()).save(any());
    }

    @Test
    void getAllBooks_Success() {
        // Given
        List<Book> books = Arrays.asList(
            validBook,
            new Book("978-0321356680", "Java: The Complete Reference", "Herbert Schildt")
        );
        when(bookRepository.findAll()).thenReturn(books);

        // When
        List<Book> result = bookService.getAllBooks();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(validBook, result.get(0));
        verify(bookRepository).findAll();
    }

    @Test
    void getAllBooks_EmptyList() {
        // Given
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Book> result = bookService.getAllBooks();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookRepository).findAll();
    }

    @Test
    void getBookById_Success() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(validBook));

        // When
        Optional<Book> result = bookService.getBookById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(validBook, result.get());
        verify(bookRepository).findById(1L);
    }

    @Test
    void getBookById_NotFound() {
        // Given
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Book> result = bookService.getBookById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(bookRepository).findById(999L);
    }

    @Test
    void getBookById_NullId_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookService.getBookById(null)
        );
        assertEquals("Book ID cannot be null", exception.getMessage());
        verify(bookRepository, never()).findById(any());
    }
}
