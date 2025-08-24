# Library Management System API

A RESTful API for managing a simple library system built with Java 17 and Spring Boot.

**Last Updated:** January 24, 2025

## Features

- **5 Core API Endpoints**: Register borrowers/books, get all books, borrow/return books
- **Multi-environment configuration**: Development, production, and Docker profiles
- **Enhanced type safety**: Sealed classes, generics, and Java 17 records
- **Comprehensive validation**: Bean Validation with custom ISBN format validation
- **Auto-generated documentation**: OpenAPI 3.0 with Swagger UI
- **Container-ready**: Docker and Kubernetes deployment configurations
- **Production-ready**: Health checks, auto-scaling, persistent storage

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.5**
- **PostgreSQL 15**
- **Maven** for dependency management
- **Docker & Docker Compose** for containerization
- **Kubernetes** for orchestration and scaling
- **Spring Data JPA** for data persistence
- **Bean Validation** for input validation
- **SpringDoc OpenAPI** for API documentation
- **Spring Boot Actuator** for monitoring

## Database Choice Justification

**PostgreSQL** was chosen as the database for the following reasons:

1. **ACID Compliance**: Ensures data consistency for critical operations like borrowing/returning books
2. **Relational Structure**: Perfect fit for the normalized data model with clear relationships between borrowers, books, and borrow records
3. **Concurrent Access**: Handles multiple users borrowing/returning books simultaneously
4. **Scalability**: Can handle growing library collections and user base
5. **JSON Support**: Future extensibility for storing additional metadata
6. **Open Source**: No licensing costs and strong community support

## Project Structure

```
librarySystem/
├── librarySystem_Backend/           # Spring Boot application
│   ├── src/main/java/com/assessment/librarySystem/
│   │   ├── controller/             # REST endpoints (LibraryController)
│   │   ├── service/                # Business logic layer
│   │   │   ├── interfaces/         # Service interfaces
│   │   │   └── impl/               # Service implementations
│   │   ├── repository/             # Data access layer (JPA repositories)
│   │   ├── model/                  # Entity classes (Book, Borrower, BorrowRecord)
│   │   ├── dto/                    # Data Transfer Objects
│   │   │   ├── request/            # Request DTOs
│   │   │   └── response/           # Response DTOs
│   │   ├── mapper/                 # Entity-DTO mappers
│   │   ├── config/                 # Configuration classes (OpenAPI)
│   │   ├── common/                 # Common utilities (OperationResult)
│   │   └── exception/              # Error handling
│   ├── src/main/resources/
│   │   ├── application.yml         # Main configuration
│   │   ├── application.properties  # Default profile (Docker)
│   │   ├── application-dev.properties  # Development profile
│   │   ├── application-prod.properties # Production profile
│   │   └── static/
│   │       └── postman-collection.json # Postman collection
│   ├── src/test/java/              # Unit and integration tests
│   ├── Dockerfile                  # Container build configuration
│   └── pom.xml                     # Maven dependencies
├── k8s/                            # Kubernetes deployment manifests
│   ├── namespace.yaml              # Kubernetes namespace
│   ├── configmap.yaml              # Application configuration
│   ├── secret.yaml                 # Database credentials
│   ├── postgres-pvc.yaml           # PostgreSQL persistent volume
│   ├── postgres-deployment.yaml    # PostgreSQL deployment
│   ├── postgres-service.yaml       # PostgreSQL service
│   ├── app-deployment.yaml         # Application deployment
│   ├── app-service.yaml            # Application service
│   ├── ingress.yaml                # External access configuration
│   ├── hpa.yaml                    # Horizontal Pod Autoscaler
│   └── kustomization.yaml          # Kustomize configuration
├── docker-compose.yml              # Docker Compose for local development
├── build-and-push.sh               # Docker build and push script
├── openapi.yml                     # OpenAPI 3.0 specification
├── API_DOCUMENTATION.md            # Comprehensive API documentation
├── POSTMAN_SETUP.md                # Postman testing guide
├── KUBERNETES_SETUP.md             # Kubernetes deployment guide
└── README.md                       # Project overview and setup
```

## Data Models

### Borrower
- `id` (Long): Unique identifier
- `name` (String): Borrower's name (2-100 characters)
- `email` (String): Unique email address

### Book
- `id` (Long): Unique identifier
- `isbn` (String): ISBN number (10-17 characters)
- `title` (String): Book title (1-255 characters)
- `author` (String): Book author (1-255 characters)

### BorrowRecord
- `id` (Long): Unique identifier
- `borrower` (Borrower): Reference to borrower
- `book` (Book): Reference to book
- `borrowDate` (LocalDateTime): When book was borrowed
- `returnDate` (LocalDateTime): When book was returned (null if not returned)
- `isReturned` (Boolean): Return status

## Business Rules & Assumptions

1. **ISBN Consistency**: Books with the same ISBN must have identical title and author
2. **Multiple Copies**: Multiple books with the same ISBN are allowed (different IDs)
3. **Single Borrower**: Only one borrower can have a specific book (by ID) at a time
4. **Email Uniqueness**: Each borrower must have a unique email address
5. **Automatic Timestamps**: Borrow date is set automatically when borrowing
6. **Return Validation**: Only the borrower who borrowed a book can return it

## API Endpoints

The system provides exactly **5 core endpoints**:

### 1. Register a New Borrower
```http
POST /api/library/borrowers
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

### 2. Register a New Book
```http
POST /api/library/books
Content-Type: application/json

{
  "isbn": "978-0134685991",
  "title": "Effective Java",
  "author": "Joshua Bloch"
}
```

### 3. Get All Books
```http
GET /api/library/books
```

### 4. Borrow a Book
```http
POST /api/library/borrow/{borrowerId}/{bookId}
```

### 5. Return a Book
```http
POST /api/library/return/{borrowerId}/{bookId}
```

## Deployment Options

### 🐳 Docker Compose (Recommended for Development)
```bash
# Clone and navigate to project
git clone <repository-url>
cd librarySystem

# Start all services
docker compose up -d --build

# Access the application
# API: http://localhost:8081/api/library
# Swagger UI: http://localhost:8081/swagger-ui.html
```

### ☸️ Kubernetes (Production Deployment)
```bash
# Build and push Docker image
./build-and-push.sh your-registry.com latest

# Deploy to Kubernetes cluster
kubectl apply -k k8s/

# Check deployment status
kubectl get pods -n library-system
```

### 💻 Local Development
```bash
# Prerequisites: Java 17, Maven 3.6+, PostgreSQL

# Start PostgreSQL locally
# Configure database connection in application-dev.properties

# Run the application
cd librarySystem_Backend
mvn spring-boot:run -Dspring.profiles.active=dev
```

## Testing & Documentation

### 📖 API Documentation
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs
- **Static OpenAPI**: `openapi.yml` file in project root

### 🧪 Testing with Postman
1. **Import from URL**: `http://localhost:8081/v3/api-docs`
2. **Import static file**: `openapi.yml`
3. **Import collection**: `librarySystem_Backend/src/main/resources/static/postman-collection.json`

See `POSTMAN_SETUP.md` for detailed testing instructions.

## Configuration Profiles

- **Default** (`application.properties`): Docker environment (port 8081)
- **Development** (`application-dev.properties`): Local development with debug logging
- **Production** (`application-prod.properties`): Production optimizations and security

## Environment Variables

### Docker/Kubernetes
```bash
DB_USERNAME=postgres
DB_PASSWORD=postgres
SERVER_PORT=8081
```

### Production
```bash
DATABASE_URL=jdbc:postgresql://your-db-host:5432/library_db
DB_USERNAME=your-username
DB_PASSWORD=your-secure-password
SERVER_PORT=8081
```

## Error Handling

The API provides comprehensive error responses:

- **400 Bad Request**: Validation errors or invalid input
- **404 Not Found**: Resource not found
- **409 Conflict**: Business rule violations (e.g., book already borrowed)
- **500 Internal Server Error**: Unexpected server errors

Example error response:
```json
{
  "status": 400,
  "message": "A borrower with this email already exists",
  "timestamp": "2025-08-23T17:00:00"
}
```

## Testing the API

### Sample API Calls

1. **Register a borrower**:
```bash
curl -X POST http://localhost:8081/api/library/borrowers \
  -H "Content-Type: application/json" \
  -d '{"name": "Alice Smith", "email": "alice@example.com"}'
```

2. **Register a book**:
```bash
curl -X POST http://localhost:8081/api/library/books \
  -H "Content-Type: application/json" \
  -d '{"isbn": "978-0134685991", "title": "Effective Java", "author": "Joshua Bloch"}'
```

3. **Get all books**:
```bash
curl -X GET http://localhost:8081/api/library/books
```

4. **Borrow a book**:
```bash
curl -X POST http://localhost:8081/api/library/borrow/1/1
```

5. **Return a book**:
```bash
curl -X POST http://localhost:8081/api/library/return/1/1
```

## Additional Resources

- **`API_DOCUMENTATION.md`**: Comprehensive API endpoint documentation
- **`POSTMAN_SETUP.md`**: Step-by-step Postman testing guide  
- **`KUBERNETES_SETUP.md`**: Complete Kubernetes deployment guide
- **`k8s/`**: Production-ready Kubernetes manifests
- **`build-and-push.sh`**: Docker build and registry push automation

## Health Monitoring

The application includes Spring Boot Actuator for health monitoring:
```http
GET /actuator/health
```

Available in all deployment modes for monitoring application and database connectivity.

## Architecture Highlights

- **Clean Architecture**: Separation of concerns with distinct layers
- **Type Safety**: Java 17 records, sealed classes, and generics
- **Validation**: Bean Validation with custom ISBN format validation
- **Error Handling**: Structured error responses with proper HTTP status codes
- **Scalability**: Kubernetes-ready with auto-scaling and health checks
- **Documentation**: Auto-generated OpenAPI 3.0 specification
- **Testing**: Comprehensive test setup and Postman collections
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
