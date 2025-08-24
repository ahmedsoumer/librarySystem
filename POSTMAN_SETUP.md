# Postman Setup Guide for Library Management System API

This guide helps your colleagues set up and test the 5 core endpoints of the Library Management System API using Postman.

## Quick Setup Options

### Option 1: Import OpenAPI Specification (Recommended)
1. Start the application: `mvn spring-boot:run` (from `librarySystem_Backend/` directory)
2. Open Postman
3. Click **Import** → **Link** 
4. Enter: `http://localhost:8081/v3/api-docs`
5. Click **Continue** → **Import**

### Option 2: Import Static OpenAPI File
1. Open Postman
2. Click **Import** → **File**
3. Select `openapi.yml` from the project root directory
4. Click **Import**

### Option 3: Import Postman Collection
1. Open Postman
2. Click **Import** → **File**
3. Select `librarySystem_Backend/src/main/resources/static/postman-collection.json`
4. Click **Import**

## Testing the API

### Prerequisites
- Ensure PostgreSQL is running (or use Docker Compose)
- Start the Spring Boot application on port 8081

### Test Sequence
Execute the requests in this order for a complete workflow:

1. **Register Borrower** - `POST /api/library/borrowers`
2. **Register Book** - `POST /api/library/books`  
3. **Get All Books** - `GET /api/library/books`
4. **Borrow Book** - `POST /api/library/borrow/{borrowerId}/{bookId}`
5. **Return Book** - `POST /api/library/return/{borrowerId}/{bookId}`

### Sample Test Data

**Borrower Registration:**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

**Book Registration:**
```json
{
  "isbn": "978-0134685991",
  "title": "Effective Java",
  "author": "Joshua Bloch"
}
```

## API Documentation Access

Once the application is running, access the interactive documentation:

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs
- **OpenAPI YAML**: http://localhost:8081/v3/api-docs.yaml

## Environment Variables

Set up a Postman environment with:
- `baseUrl`: `http://localhost:8081`

## Expected Response Codes

- **201 Created**: Successful POST operations (register borrower/book, borrow book)
- **200 OK**: Successful GET operations and return book
- **400 Bad Request**: Invalid input data
- **409 Conflict**: Business rule violations (book not available, etc.)

## Troubleshooting

1. **Connection refused**: Ensure the application is running on port 8081
2. **Database errors**: Check PostgreSQL connection and credentials
3. **Validation errors**: Verify request body format matches the schema
4. **404 errors**: Ensure you're using the correct endpoint paths

## Business Rules to Test

1. **Borrower email must be unique**
2. **Book ISBN must be unique and follow format: 978-xxxxxxxxxx**
3. **Cannot borrow an already borrowed book**
4. **Cannot return a book that wasn't borrowed**
5. **Only the borrower who borrowed a book can return it**
