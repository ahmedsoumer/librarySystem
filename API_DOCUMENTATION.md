# Library Management System API Documentation

This document provides comprehensive documentation for the Library Management System REST API.


## Base URL
```
http://localhost:8081
```

## Authentication
Currently, no authentication is required for API endpoints.

## Content Type
All API endpoints accept and return JSON data with `Content-Type: application/json`.

## Error Handling
The API uses standard HTTP status codes and returns structured error responses:

```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/library/borrowers"
}
```

## Endpoints

The API implements exactly 5 endpoints as specified:

### 1. Register a New Borrower
**POST** `/api/library/borrowers`

Register a new borrower in the system.

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

**Validation Rules:**
- `name`: Required, 2-100 characters
- `email`: Required, valid email format, unique

### 2. Register a New Book
**POST** `/api/library/books`

Register a new book in the library system.

**Request Body:**
```json
{
  "isbn": "978-0134685991",
  "title": "Effective Java",
  "author": "Joshua Bloch"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "isbn": "978-0134685991",
  "title": "Effective Java",
  "author": "Joshua Bloch",
  "isAvailable": true
}
```

**Validation Rules:**
- `isbn`: Required, valid ISBN format
- `title`: Required, 1-200 characters
- `author`: Required, 1-100 characters
- Books with same ISBN must have same title and author

### 3. Get a List of All Books
**GET** `/api/library/books`

Retrieve a list of all books in the library.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "isbn": "978-0134685991",
    "title": "Effective Java",
    "author": "Joshua Bloch",
    "isAvailable": true
  },
  {
    "id": 2,
    "isbn": "978-0321356680",
    "title": "Java: The Complete Reference",
    "author": "Herbert Schildt",
    "isAvailable": false
  }
]
```

### 4. Borrow a Book
**POST** `/api/library/borrow/{borrowerId}/{bookId}`

Allow a borrower to borrow a specific book.

**Path Parameters:**
- `borrowerId` (Long): The borrower's unique identifier
- `bookId` (Long): The book's unique identifier

**Response (201 Created):**
```json
{
  "borrowRecordId": 1,
  "borrowerId": 1,
  "borrowerName": "John Doe",
  "bookId": 1,
  "bookTitle": "Effective Java",
  "bookAuthor": "Joshua Bloch",
  "operationDate": "2024-01-15T10:30:00",
  "operation": "BORROWED",
  "message": "Book 'Effective Java' has been successfully borrowed by John Doe"
}
```

**Business Rules:**
- Borrower must exist
- Book must exist
- Book must be available (not currently borrowed)
- Only one borrower can borrow a specific book instance at a time

### 5. Return a Borrowed Book
**POST** `/api/library/return/{borrowerId}/{bookId}`

Allow a borrower to return a previously borrowed book.

**Path Parameters:**
- `borrowerId` (Long): The borrower's unique identifier
- `bookId` (Long): The book's unique identifier

**Response (200 OK):**
```json
{
  "borrowRecordId": 1,
  "borrowerId": 1,
  "borrowerName": "John Doe",
  "bookId": 1,
  "bookTitle": "Effective Java",
  "bookAuthor": "Joshua Bloch",
  "operationDate": "2024-01-15T14:30:00",
  "operation": "RETURNED",
  "message": "Book 'Effective Java' has been successfully returned by John Doe"
}
```

**Business Rules:**
- Borrower must exist
- Book must exist
- There must be an active borrow record for this book
- Only the borrower who borrowed the book can return it

## HTTP Status Codes

- **200 OK**: Request successful
- **201 Created**: Resource created successfully
- **400 Bad Request**: Invalid request data or validation errors
- **404 Not Found**: Resource not found
- **409 Conflict**: Business rule violation (e.g., book already borrowed)
- **500 Internal Server Error**: Server error

## Data Transfer Objects (DTOs)

### Request DTOs

#### BorrowerRegistrationRequest
```json
{
  "name": "string (required, 2-100 chars)",
  "email": "string (required, valid email format)"
}
```

#### BookRegistrationRequest
```json
{
  "isbn": "string (required, valid ISBN format)",
  "title": "string (required, 1-200 chars)",
  "author": "string (required, 1-100 chars)"
}
```

### Response DTOs

#### BorrowerResponse
```json
{
  "id": "long",
  "name": "string",
  "email": "string"
}
```

#### BookResponse
```json
{
  "id": "long",
  "isbn": "string",
  "title": "string",
  "author": "string",
  "isAvailable": "boolean"
}
```

#### BorrowOperationResponse
```json
{
  "borrowRecordId": "long",
  "borrowerId": "long",
  "borrowerName": "string",
  "bookId": "long",
  "bookTitle": "string",
  "bookAuthor": "string",
  "operationDate": "datetime",
  "operation": "string (BORROWED|RETURNED)",
  "message": "string"
}
```

## Business Rules

1. **Borrower Registration**:
   - Email addresses must be unique
   - Name and email are required fields

2. **Book Registration**:
   - Books with the same ISBN must have identical title and author
   - ISBN, title, and author are required fields

3. **Borrowing**:
   - Only one borrower can borrow a specific book instance at a time
   - Both borrower and book must exist in the system
   - Book must be available (not currently borrowed)

4. **Returning**:
   - Only the borrower who borrowed the book can return it
   - There must be an active borrow record for the book
   - Return date is automatically set to current timestamp

## Examples

### Complete Workflow Example

1. **Register a borrower**:
```bash
curl -X POST http://localhost:8081/api/library/borrowers \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john@example.com"}'
```

2. **Register a book**:
```bash
curl -X POST http://localhost:8081/api/library/books \
  -H "Content-Type: application/json" \
  -d '{"isbn": "978-0134685991", "title": "Effective Java", "author": "Joshua Bloch"}'
```

3. **Get all books**:
```bash
curl http://localhost:8081/api/library/books
```

4. **Borrow the book**:
```bash
curl -X POST http://localhost:8081/api/library/borrow/1/1
```

5. **Return the book**:
```bash
curl -X POST http://localhost:8081/api/library/return/1/1
```

## Enhanced Features

- **Type Safety**: Enhanced with sealed classes and generics for better compile-time safety
- **Generic Patterns**: `OperationResult<T>`, `ValidationResult`, `BookStatus` sealed interfaces
- **Enhanced DTOs**: Generic `ApiResponse<T>` and `ErrorResponse<T>` with metadata support
- **Repository Patterns**: Generic `BaseRepository<T, ID>` with enhanced methods
- **Mapper Interfaces**: Generic `EntityMapper<E, R, C>` for consistent mapping patterns

## Error Response Format

All error responses follow this format:

```json
{
  "status": 400,
  "message": "Detailed error message",
  "details": null,
  "timestamp": "2025-01-24T12:00:00"
}
```

For validation errors:
```json
{
  "status": 400,
  "message": "Validation failed",
  "details": {
    "name": "Name is required",
    "email": "Email should be valid"
  },
  "timestamp": "2025-01-24T12:00:00"
}
```
