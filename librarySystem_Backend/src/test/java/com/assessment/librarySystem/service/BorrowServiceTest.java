package com.assessment.librarySystem.service;

import com.assessment.librarySystem.model.Book;
import com.assessment.librarySystem.model.BorrowRecord;
import com.assessment.librarySystem.model.Borrower;
import com.assessment.librarySystem.repository.BorrowRecordRepository;
import com.assessment.librarySystem.service.impl.BorrowServiceImpl;
import com.assessment.librarySystem.service.interfaces.BookService;
import com.assessment.librarySystem.service.interfaces.BorrowerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class BorrowServiceTest {

    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @Mock
    private BorrowerService borrowerService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BorrowServiceImpl borrowService;

    private Borrower validBorrower;
    private Book validBook;
    private BorrowRecord validBorrowRecord;

    @BeforeEach
    void setUp() {
        validBorrower = new Borrower("John Doe", "john@example.com");
        validBorrower.setId(1L);

        validBook = new Book("978-0134685991", "Effective Java", "Joshua Bloch");
        validBook.setId(1L);

        validBorrowRecord = new BorrowRecord(validBorrower, validBook);
        validBorrowRecord.setId(1L);
        validBorrowRecord.setBorrowDate(LocalDateTime.now());
    }

    @Test
    void borrowBook_Success() {
        // Given
        when(borrowerService.getBorrowerById(1L)).thenReturn(Optional.of(validBorrower));
        when(bookService.getBookById(1L)).thenReturn(Optional.of(validBook));
        when(borrowRecordRepository.findByBookIdAndReturnDateIsNull(1L)).thenReturn(Optional.empty());
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(validBorrowRecord);

        // When
        BorrowRecord result = borrowService.borrowBook(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(validBorrowRecord, result);
        verify(borrowerService, times(2)).getBorrowerById(1L); // Called in validation and createBorrowRecord
        verify(bookService, times(2)).getBookById(1L); // Called in validation and createBorrowRecord
        verify(borrowRecordRepository).findByBookIdAndReturnDateIsNull(1L);
        verify(borrowRecordRepository).save(any(BorrowRecord.class));
    }

    @Test
    void borrowBook_NullBorrowerId_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowService.borrowBook(null, 1L)
        );
        assertEquals("Borrower ID cannot be null", exception.getMessage());
        verify(borrowRecordRepository, never()).save(any());
    }

    @Test
    void borrowBook_NullBookId_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowService.borrowBook(1L, null)
        );
        assertEquals("Book ID cannot be null", exception.getMessage());
        verify(borrowRecordRepository, never()).save(any());
    }

    @Test
    void borrowBook_BorrowerNotFound_ThrowsException() {
        // Given
        when(borrowerService.getBorrowerById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowService.borrowBook(999L, 1L)
        );
        assertEquals("Borrower not found with ID: 999", exception.getMessage());
        verify(borrowerService).getBorrowerById(999L);
        verify(borrowRecordRepository, never()).save(any());
    }

    @Test
    void borrowBook_BookNotFound_ThrowsException() {
        // Given
        when(borrowerService.getBorrowerById(1L)).thenReturn(Optional.of(validBorrower));
        when(bookService.getBookById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowService.borrowBook(1L, 999L)
        );
        assertEquals("Book not found with ID: 999", exception.getMessage());
        verify(borrowerService).getBorrowerById(1L);
        verify(bookService).getBookById(999L);
        verify(borrowRecordRepository, never()).save(any());
    }

    @Test
    void borrowBook_BookAlreadyBorrowed_ThrowsException() {
        // Given
        BorrowRecord existingBorrowRecord = new BorrowRecord(validBorrower, validBook);
        when(borrowerService.getBorrowerById(1L)).thenReturn(Optional.of(validBorrower));
        when(bookService.getBookById(1L)).thenReturn(Optional.of(validBook));
        when(borrowRecordRepository.findByBookIdAndReturnDateIsNull(1L))
            .thenReturn(Optional.of(existingBorrowRecord));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowService.borrowBook(1L, 1L)
        );
        assertEquals("Book with ID 1 is already borrowed", exception.getMessage());
        verify(borrowerService).getBorrowerById(1L);
        verify(bookService).getBookById(1L);
        verify(borrowRecordRepository).findByBookIdAndReturnDateIsNull(1L);
        verify(borrowRecordRepository, never()).save(any());
    }

    @Test
    void returnBook_Success() {
        // Given
        when(borrowerService.getBorrowerById(1L)).thenReturn(Optional.of(validBorrower));
        when(bookService.getBookById(1L)).thenReturn(Optional.of(validBook));
        when(borrowRecordRepository.findByBorrowerIdAndBookIdAndReturnDateIsNull(1L, 1L))
            .thenReturn(Optional.of(validBorrowRecord));
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(validBorrowRecord);

        // When
        BorrowRecord result = borrowService.returnBook(1L, 1L);

        // Then
        assertNotNull(result);
        assertNotNull(result.getReturnDate());
        verify(borrowerService).getBorrowerById(1L);
        verify(bookService).getBookById(1L);
        verify(borrowRecordRepository, times(2)).findByBorrowerIdAndBookIdAndReturnDateIsNull(1L, 1L); // Called in validation and main logic
        verify(borrowRecordRepository).save(validBorrowRecord);
    }

    @Test
    void returnBook_NullBorrowerId_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowService.returnBook(null, 1L)
        );
        assertEquals("Borrower ID cannot be null", exception.getMessage());
        verify(borrowRecordRepository, never()).save(any());
    }

    @Test
    void returnBook_NullBookId_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowService.returnBook(1L, null)
        );
        assertEquals("Book ID cannot be null", exception.getMessage());
        verify(borrowRecordRepository, never()).save(any());
    }

    @Test
    void returnBook_BorrowerNotFound_ThrowsException() {
        // Given
        when(borrowerService.getBorrowerById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowService.returnBook(999L, 1L)
        );
        assertEquals("Borrower not found with ID: 999", exception.getMessage());
        verify(borrowerService).getBorrowerById(999L);
        verify(borrowRecordRepository, never()).save(any());
    }

    @Test
    void returnBook_BookNotFound_ThrowsException() {
        // Given
        when(borrowerService.getBorrowerById(1L)).thenReturn(Optional.of(validBorrower));
        when(bookService.getBookById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowService.returnBook(1L, 999L)
        );
        assertEquals("Book not found with ID: 999", exception.getMessage());
        verify(borrowerService).getBorrowerById(1L);
        verify(bookService).getBookById(999L);
        verify(borrowRecordRepository, never()).save(any());
    }

    @Test
    void returnBook_NoActiveBorrowRecord_ThrowsException() {
        // Given
        when(borrowerService.getBorrowerById(1L)).thenReturn(Optional.of(validBorrower));
        when(bookService.getBookById(1L)).thenReturn(Optional.of(validBook));
        when(borrowRecordRepository.findByBorrowerIdAndBookIdAndReturnDateIsNull(1L, 1L))
            .thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowService.returnBook(1L, 1L)
        );
        assertEquals("No active borrow record found for borrower 1 and book 1", exception.getMessage());
        verify(borrowerService).getBorrowerById(1L);
        verify(bookService).getBookById(1L);
        verify(borrowRecordRepository, times(1)).findByBorrowerIdAndBookIdAndReturnDateIsNull(1L, 1L);
        verify(borrowRecordRepository, never()).save(any());
    }
}
