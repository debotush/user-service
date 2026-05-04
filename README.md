# User Service - Event Ticketing System

A Spring Boot microservice for managing users, authentication, and authorization in an event ticketing system. This service is part of the Enterprise System Integration project.

## 🚀 Features

- **User Management**: Registration, profile retrieval, and updates.
- **Authentication**: Secure login with JWT (JSON Web Token).
- **Security**: Password hashing and role-based access control (USER/ADMIN).
- **Validation**: Input validation for all registration and login requests.
- **Documentation**: Fully documented with Swagger UI/OpenAPI.
- **Containerization**: Docker and Docker Compose support.

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 3.4.5**
- **Spring Data JPA**
- **Spring Security** (JWT)
- **PostgreSQL**
- **Lombok**
- **Hibernate**
- **Springdoc-openapi** (Swagger)

## 📋 API Endpoints

The service runs on port `8090`.

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/users/register` | Register a new user |
| `POST` | `/users/login` | Authenticate and receive a JWT |
| `GET` | `/users/{id}` | Get user profile details |
| `PUT` | `/users/{id}` | Update user profile |
| `PATCH` | `/users/{id}/role` | Update user role (ADMIN only) |
| `POST` | `/users/validate` | Validate a JWT token |

## 🏃 Running Locally

### 1. Prerequisites
- Java 17+
- Maven 3.8+
- Docker (for PostgreSQL)

### 2. Start PostgreSQL
```bash
docker compose up -d db
```

### 3. Run the Application
```bash
mvn spring-boot:run
```
The application will be available at `http://localhost:8090`.

## 🐳 Running with Docker

To run the entire stack (App + DB) in containers:

```bash
docker compose up --build
```

## 📖 API Documentation

Once the service is running, you can access the Swagger UI to explore and test the endpoints:
- **Swagger UI**: [http://localhost:8090/swagger-ui/index.html](http://localhost:8090/swagger-ui/index.html)
- **API Docs**: `http://localhost:8090/v3/api-docs`

## 🧪 Testing

To run the unit tests (using H2 in-memory database):
```bash
mvn test
```
The tests include happy path and error case scenarios for the controller layer using `@WebMvcTest`.
