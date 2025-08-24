package com.assessment.librarySystem.service;

import com.assessment.librarySystem.model.Borrower;
import com.assessment.librarySystem.repository.BorrowerRepository;
import com.assessment.librarySystem.service.impl.BorrowerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class BorrowerServiceTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BorrowerServiceImpl borrowerService;

    private Borrower validBorrower;

    @BeforeEach
    void setUp() {
        validBorrower = new Borrower("John Doe", "john@example.com");
        validBorrower.setId(1L);
    }

    @Test
    void registerBorrower_Success() {
        // Given
        Borrower newBorrower = new Borrower("Jane Smith", "jane@example.com");
        when(borrowerRepository.existsByEmail(anyString())).thenReturn(false);
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(validBorrower);

        // When
        Borrower result = borrowerService.registerBorrower(newBorrower);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(borrowerRepository).existsByEmail("jane@example.com");
        verify(borrowerRepository).save(newBorrower);
    }

    @Test
    void registerBorrower_NullBorrower_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowerService.registerBorrower(null)
        );
        assertEquals("Borrower cannot be null", exception.getMessage());
        verify(borrowerRepository, never()).save(any());
    }

    @Test
    void registerBorrower_EmptyName_ThrowsException() {
        // Given
        Borrower invalidBorrower = new Borrower("", "john@example.com");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowerService.registerBorrower(invalidBorrower)
        );
        assertEquals("Borrower name is required", exception.getMessage());
        verify(borrowerRepository, never()).save(any());
    }

    @Test
    void registerBorrower_NameTooShort_ThrowsException() {
        // Given
        Borrower invalidBorrower = new Borrower("A", "john@example.com");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowerService.registerBorrower(invalidBorrower)
        );
        assertEquals("Borrower name must be between 2 and 100 characters", exception.getMessage());
        verify(borrowerRepository, never()).save(any());
    }

    @Test
    void registerBorrower_NameTooLong_ThrowsException() {
        // Given
        String longName = "A".repeat(101);
        Borrower invalidBorrower = new Borrower(longName, "john@example.com");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowerService.registerBorrower(invalidBorrower)
        );
        assertEquals("Borrower name must be between 2 and 100 characters", exception.getMessage());
        verify(borrowerRepository, never()).save(any());
    }

    @Test
    void registerBorrower_EmptyEmail_ThrowsException() {
        // Given
        Borrower invalidBorrower = new Borrower("John Doe", "");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowerService.registerBorrower(invalidBorrower)
        );
        assertEquals("Borrower email is required", exception.getMessage());
        verify(borrowerRepository, never()).save(any());
    }

    @Test
    void registerBorrower_InvalidEmailFormat_ThrowsException() {
        // Given
        Borrower invalidBorrower = new Borrower("John Doe", "invalid-email");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowerService.registerBorrower(invalidBorrower)
        );
        assertEquals("Invalid email format", exception.getMessage());
        verify(borrowerRepository, never()).save(any());
    }

    @Test
    void registerBorrower_DuplicateEmail_ThrowsException() {
        // Given
        Borrower newBorrower = new Borrower("Jane Smith", "john@example.com");
        when(borrowerRepository.existsByEmail("john@example.com")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowerService.registerBorrower(newBorrower)
        );
        assertEquals("Email already exists: john@example.com", exception.getMessage());
        verify(borrowerRepository).existsByEmail("john@example.com");
        verify(borrowerRepository, never()).save(any());
    }

    @Test
    void getBorrowerById_Success() {
        // Given
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(validBorrower));

        // When
        Optional<Borrower> result = borrowerService.getBorrowerById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(validBorrower, result.get());
        verify(borrowerRepository).findById(1L);
    }

    @Test
    void getBorrowerById_NotFound() {
        // Given
        when(borrowerRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Borrower> result = borrowerService.getBorrowerById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(borrowerRepository).findById(999L);
    }

    @Test
    void getBorrowerById_NullId_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> borrowerService.getBorrowerById(null)
        );
        assertEquals("Borrower ID cannot be null", exception.getMessage());
        verify(borrowerRepository, never()).findById(any());
    }
}
