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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Main controller for the Library Management System API
 * Implements only the required endpoints as specified
 */
@RestController
@RequestMapping("/api/library")
@Tag(name = "Library Management", description = "API for managing library books and borrowers")
public class LibraryController {
    
    @Autowired
    private BorrowerService borrowerService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private BorrowService borrowService;
    
    @Autowired
    private BorrowerMapper borrowerMapper;
    
    @Autowired
    private BookMapper bookMapper;
    
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    
    /**
     * Register a new borrower
     * POST /api/library/borrowers
     */
    @Operation(summary = "Register a new borrower", description = "Creates a new borrower in the library system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Borrower registered successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BorrowerResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/borrowers")
    public ResponseEntity<BorrowerResponse> registerBorrower(@Valid @RequestBody BorrowerRegistrationRequest request) {
        Borrower borrower = borrowerMapper.toEntity(request);
        Borrower registeredBorrower = borrowerService.registerBorrower(borrower);
        BorrowerResponse response = borrowerMapper.toResponse(registeredBorrower);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Register a new book
     * POST /api/library/books
     */
    @Operation(summary = "Register a new book", description = "Adds a new book to the library catalog")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Book registered successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/books")
    public ResponseEntity<BookResponse> registerBook(@Valid @RequestBody BookRegistrationRequest request) {
        Book book = bookMapper.toEntity(request);
        Book registeredBook = bookService.registerBook(book);
        BookResponse response = bookMapper.toResponse(registeredBook);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Get a list of all books
     * GET /api/library/books
     */
    @Operation(summary = "Get all books", description = "Retrieves a list of all books in the library")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Books retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponse.class)))
    })
    @GetMapping("/books")
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        List<BookResponse> response = bookMapper.toResponseList(books);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Borrow a book
     * POST /api/library/borrow/{borrowerId}/{bookId}
     */
    @Operation(summary = "Borrow a book", description = "Creates a borrow record for a borrower and book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Book borrowed successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BorrowOperationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid borrower or book ID"),
        @ApiResponse(responseCode = "409", description = "Book is not available for borrowing")
    })
    @PostMapping("/borrow/{borrowerId}/{bookId}")
    public ResponseEntity<BorrowOperationResponse> borrowBook(
            @Parameter(description = "ID of the borrower") @PathVariable Long borrowerId, 
            @Parameter(description = "ID of the book to borrow") @PathVariable Long bookId) {
        BorrowRecord borrowRecord = borrowService.borrowBook(borrowerId, bookId);
        BorrowOperationResponse response = borrowRecordMapper.toBorrowOperationResponse(borrowRecord);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Return a borrowed book
     * POST /api/library/return/{borrowerId}/{bookId}
     */
    @Operation(summary = "Return a book", description = "Marks a borrowed book as returned")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book returned successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BorrowOperationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid borrower or book ID"),
        @ApiResponse(responseCode = "409", description = "No active borrow record found")
    })
    @PostMapping("/return/{borrowerId}/{bookId}")
    public ResponseEntity<BorrowOperationResponse> returnBook(
            @Parameter(description = "ID of the borrower") @PathVariable Long borrowerId, 
            @Parameter(description = "ID of the book to return") @PathVariable Long bookId) {
        BorrowRecord borrowRecord = borrowService.returnBook(borrowerId, bookId);
        BorrowOperationResponse response = borrowRecordMapper.toReturnOperationResponse(borrowRecord);
        return ResponseEntity.ok(response);
    }
}
