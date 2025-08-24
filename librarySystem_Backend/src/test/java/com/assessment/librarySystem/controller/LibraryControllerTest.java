package com.assessment.librarySystem.controller;

import com.assessment.librarySystem.dto.request.BookRegistrationRequest;
import com.assessment.librarySystem.dto.request.BorrowerRegistrationRequest;
import com.assessment.librarySystem.dto.response.BookResponse;
import com.assessment.librarySystem.dto.response.BorrowOperationResponse;
import com.assessment.librarySystem.dto.response.BorrowerResponse;
import com.assessment.librarySystem.mapper.BookMapper;
import com.assessment.librarySystem.mapper.BorrowRecordMapper;
import com.assessment.librarySystem.mapper.BorrowerMapper;
import com.assessment.librarySystem.model.Book;
import com.assessment.librarySystem.model.BorrowRecord;
import com.assessment.librarySystem.model.Borrower;
import com.assessment.librarySystem.service.interfaces.BookService;
import com.assessment.librarySystem.service.interfaces.BorrowService;
import com.assessment.librarySystem.service.interfaces.BorrowerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LibraryController.class)
@ActiveProfiles("test")
class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BorrowerService borrowerService;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private BorrowService borrowService;

    @MockitoBean
    private BorrowerMapper borrowerMapper;

    @MockitoBean
    private BookMapper bookMapper;

    @MockitoBean
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerBorrower_Success() throws Exception {
        // Given
        BorrowerRegistrationRequest request = new BorrowerRegistrationRequest("John Doe", "john@example.com");
        Borrower borrower = new Borrower("John Doe", "john@example.com");
        borrower.setId(1L);
        BorrowerResponse response = new BorrowerResponse(1L, "John Doe", "john@example.com");

        when(borrowerMapper.toEntity(request)).thenReturn(borrower);
        when(borrowerService.registerBorrower(any(Borrower.class))).thenReturn(borrower);
        when(borrowerMapper.toResponse(borrower)).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/library/borrowers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void registerBorrower_ValidationError() throws Exception {
        // Given
        BorrowerRegistrationRequest invalidRequest = new BorrowerRegistrationRequest("", "");

        // When & Then
        mockMvc.perform(post("/api/library/borrowers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerBook_Success() throws Exception {
        // Given
        BookRegistrationRequest request = new BookRegistrationRequest("978-0134685991", "Effective Java", "Joshua Bloch");
        Book book = new Book("978-0134685991", "Effective Java", "Joshua Bloch");
        book.setId(1L);
        BookResponse response = new BookResponse(1L, "978-0134685991", "Effective Java", "Joshua Bloch", true);

        when(bookMapper.toEntity(request)).thenReturn(book);
        when(bookService.registerBook(any(Book.class))).thenReturn(book);
        when(bookMapper.toResponse(book)).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/library/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isbn").value("978-0134685991"))
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.author").value("Joshua Bloch"))
                .andExpect(jsonPath("$.isAvailable").value(true));
    }

    @Test
    void registerBook_ValidationError() throws Exception {
        // Given
        BookRegistrationRequest invalidRequest = new BookRegistrationRequest("", "", "");

        // When & Then
        mockMvc.perform(post("/api/library/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBooks_Success() throws Exception {
        // Given
        List<Book> books = Arrays.asList(
            createSampleBook(1L, "978-0134685991", "Effective Java", "Joshua Bloch"),
            createSampleBook(2L, "978-0321356680", "Java: The Complete Reference", "Herbert Schildt")
        );

        List<BookResponse> responses = Arrays.asList(
            new BookResponse(1L, "978-0134685991", "Effective Java", "Joshua Bloch", true),
            new BookResponse(2L, "978-0321356680", "Java: The Complete Reference", "Herbert Schildt", false)
        );

        when(bookService.getAllBooks()).thenReturn(books);
        when(bookMapper.toResponseList(books)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/api/library/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Effective Java"))
                .andExpect(jsonPath("$[1].title").value("Java: The Complete Reference"));
    }

    @Test
    void borrowBook_Success() throws Exception {
        // Given
        BorrowRecord borrowRecord = createSampleBorrowRecord();
        BorrowOperationResponse response = new BorrowOperationResponse(
            1L, 1L, "John Doe", 1L, "Effective Java", "Joshua Bloch", 
            LocalDateTime.now(), "BORROWED", "Book borrowed successfully"
        );

        when(borrowService.borrowBook(1L, 1L)).thenReturn(borrowRecord);
        when(borrowRecordMapper.toBorrowOperationResponse(borrowRecord)).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/library/borrow/1/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.operation").value("BORROWED"))
                .andExpect(jsonPath("$.message").value("Book borrowed successfully"))
                .andExpect(jsonPath("$.borrowerId").value(1))
                .andExpect(jsonPath("$.bookId").value(1));
    }

    @Test
    void returnBook_Success() throws Exception {
        // Given
        BorrowRecord borrowRecord = createSampleBorrowRecord();
        borrowRecord.setReturnDate(LocalDateTime.now());
        BorrowOperationResponse response = new BorrowOperationResponse(
            1L, 1L, "John Doe", 1L, "Effective Java", "Joshua Bloch",
            LocalDateTime.now(), "RETURNED", "Book returned successfully"
        );

        when(borrowService.returnBook(1L, 1L)).thenReturn(borrowRecord);
        when(borrowRecordMapper.toReturnOperationResponse(borrowRecord)).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/library/return/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("RETURNED"))
                .andExpect(jsonPath("$.message").value("Book returned successfully"))
                .andExpect(jsonPath("$.borrowerId").value(1))
                .andExpect(jsonPath("$.bookId").value(1));
    }

    @Test
    void borrowBook_BorrowerNotFound() throws Exception {
        // Given
        when(borrowService.borrowBook(999L, 1L))
            .thenThrow(new IllegalArgumentException("Borrower not found with id: 999"));

        // When & Then
        mockMvc.perform(post("/api/library/borrow/999/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void borrowBook_BookNotFound() throws Exception {
        // Given
        when(borrowService.borrowBook(1L, 999L))
            .thenThrow(new IllegalArgumentException("Book not found with id: 999"));

        // When & Then
        mockMvc.perform(post("/api/library/borrow/1/999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void borrowBook_BookAlreadyBorrowed() throws Exception {
        // Given
        when(borrowService.borrowBook(1L, 1L))
            .thenThrow(new IllegalStateException("Book is already borrowed by another member"));

        // When & Then
        mockMvc.perform(post("/api/library/borrow/1/1"))
                .andExpect(status().isConflict());
    }

    private Book createSampleBook(Long id, String isbn, String title, String author) {
        Book book = new Book(isbn, title, author);
        book.setId(id);
        return book;
    }

    private BorrowRecord createSampleBorrowRecord() {
        Borrower borrower = new Borrower("John Doe", "john@example.com");
        borrower.setId(1L);
        
        Book book = new Book("978-0134685991", "Effective Java", "Joshua Bloch");
        book.setId(1L);
        
        BorrowRecord borrowRecord = new BorrowRecord(borrower, book);
        borrowRecord.setId(1L);
        borrowRecord.setBorrowDate(LocalDateTime.now());
        
        return borrowRecord;
    }
}
