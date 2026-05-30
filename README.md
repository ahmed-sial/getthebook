# 📚 GetTheBook

A **Book Social Network** REST API built with **Spring Boot 4** and **Java 21**. Users can register, add books to their personal library, share books with other users, and manage book-sharing requests — all secured with stateless JWT authentication.

---

## 📑 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Data Model](#data-model)
- [API Overview](#api-overview)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
  - [Option A — Docker Compose (recommended)](#option-a--docker-compose-recommended)
  - [Option B — Run locally](#option-b--run-locally)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Running Tests](#running-tests)
- [Contributing](#contributing)

---

## Features

- **User accounts** — register and log in with email + password
- **Book library** — create, update, delete, and browse books with pagination
- **Sharing controls** — toggle a book's shareable/archived status
- **Book sharing** — share a book directly with another user (with expiry)
- **Share appeals** — request to borrow a book; owner can approve or reject
- **JWT security** — stateless authentication with role-based method-level access control
- **Custom permission evaluator** — fine-grained `@PreAuthorize` checks via `AppPermissionEvaluator`
- **Full audit trail** — all entities track `createdBy`, `updatedBy`, `createdAt`, `updatedAt`
- **OpenAPI / Swagger UI** — interactive API docs out of the box
- **Email support** — Spring Mail integration for transactional notifications
- **Testcontainers** — integration tests spin up a real PostgreSQL container automatically

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.3 |
| Security | Spring Security + JWT (`jjwt 0.13`) |
| Persistence | Spring Data JPA (Hibernate) |
| Database | PostgreSQL 16 |
| In-memory DB (tests) | H2 |
| Template engine | Thymeleaf |
| Validation | Spring Validation (Bean Validation 3) |
| API Documentation | SpringDoc OpenAPI 3 (Swagger UI) |
| Email | Spring Boot Mail |
| Logging | SLF4J + Logback |
| Boilerplate reduction | Lombok |
| Testing | JUnit 5, Mockito, AssertJ, Testcontainers |
| Build | Maven (Maven Wrapper included) |
| Containerisation | Docker, Docker Compose |

---

## Data Model

```
┌──────────┐       ┌──────────┐       ┌────────────────┐
│   Role   │◄──────│   User   │◄──────│   BookShare    │
└──────────┘       └────┬─────┘       │ (sharedTo,     │
                        │             │  book, expiry) │
                   ┌────▼─────┐       └────────────────┘
                   │   Book   │◄──────┌──────────────────┐
                   │(title,   │       │  BookShareAppeal  │
                   │ isbn,    │       │ (appealBy, book,  │
                   │ genre,   │       │  status, days)    │
                   │ author,  │       └──────────────────┘
                   │ shareable│
                   │ archived)│
                   └──────────┘
```

All entities extend `BaseEntity`, which provides a UUID primary key and JPA auditing fields (`createdBy`, `updatedBy`, `createdAt`, `updatedAt`).

### Entity summary

| Entity | Key fields |
|---|---|
| `User` | `firstName`, `lastName`, `email`, `password`, `role`, `isAccountEnabled`, `isAccountLocked` |
| `Role` | `name` (e.g. `ROLE_USER`) |
| `Book` | `title`, `isbn`, `genre`, `author`, `synopsis`, `publisher`, `publicationDate`, `isShareable`, `isArchived` |
| `BookShare` | `user` (recipient), `book`, `sharedAt`, `expiresAt` |
| `BookShareAppeal` | `user` (requester), `book`, `status` (`PENDING`/`APPROVED`/`REJECTED`), `days` |

---

## API Overview

All endpoints are prefixed with `/api/v1`.

### Authentication — `/auth`

| Method | Endpoint | Auth required | Description |
|---|---|---|---|
| `POST` | `/auth/register` | No | Register a new user |
| `POST` | `/auth/login` | No | Log in and receive a JWT |

### Books — `/books`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/books` | Paginated list of all books (excluding the current user's own) |
| `GET` | `/books/me` | Paginated list of the current user's books |
| `GET` | `/books/{book-id}` | Fetch a single book by ID |
| `POST` | `/books` | Create a new book |
| `PATCH` | `/books/{book-id}` | Update a book |
| `DELETE` | `/books/{book-id}` | Delete a book |
| `PATCH` | `/books/{book-id}/share-toggle` | Toggle the book's shareable status |
| `PATCH` | `/books/{book-id}/archive-toggle` | Toggle the book's archived status |
| `GET` | `/books/{book-id}/shares` | Paginated list of share records for a book |

### Book Shares — `/shares`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/shares/{share-id}` | Fetch a single share record |

### Book Share Appeals — `/appeals`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/appeals` | Submit a new share appeal (borrow request) |
| `GET` | `/appeals` | Paginated list of current user's appeals |
| `GET` | `/appeals/{appeal-id}` | Fetch a single appeal |
| `DELETE` | `/appeals/{appeal-id}` | Cancel an appeal |
| `PATCH` | `/appeals/{appeal-id}/approve` | Approve an appeal (book owner) |
| `PATCH` | `/appeals/{appeal-id}/reject` | Reject an appeal (book owner) |

> All endpoints except `/auth/**` and Swagger paths require a valid `Authorization: Bearer <token>` header.

---

## Project Structure

```
getthebook/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── mvnw / mvnw.cmd
└── src/
    ├── main/
    │   ├── java/com/ahmedhassan/getthebook/
    │   │   ├── GetthebookApplication.java
    │   │   ├── annotations/           # Custom OpenAPI annotation shortcuts
    │   │   ├── controllers/           # REST controllers (Auth, Book, BookShare, BookShareAppeal)
    │   │   ├── dtos/                  # Request/response DTOs
    │   │   │   ├── requests/
    │   │   │   └── responses/
    │   │   ├── entities/              # JPA entities (User, Book, BookShare, BookShareAppeal, Role)
    │   │   ├── enums/                 # BookAppealStatus and others
    │   │   ├── security/              # JWT filter, auth provider, permission evaluator
    │   │   │   ├── filters/
    │   │   │   └── permissions/
    │   │   ├── services/              # Business logic (AuthService, BookService, BookShareService, …)
    │   │   └── utils/                 # Utility helpers (e.g. email masking)
    │   └── resources/
    │       ├── application.yaml       # Base config (context path, multipart limits)
    │       └── application-dev.yaml   # Dev profile (DB, JWT secret, logging)
    └── test/
        └── java/com/ahmedhassan/getthebook/
```

---

## Prerequisites

- [Java 21+](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/) — or use the included `./mvnw`
- [Docker](https://www.docker.com/get-started) & [Docker Compose](https://docs.docker.com/compose/)

---

## Getting Started

### Option A — Docker Compose (recommended)

This runs the API and PostgreSQL together, no local Java install required.

**1. Build the Docker image**

```bash
docker build -t bsn/bsn:1.0.0 .
```

**2. Start all services**

```bash
docker-compose up
```

The API will be available at `http://localhost:8080/api/v1`.

**3. Stop everything**

```bash
docker-compose down
```

---

### Option B — Run locally

**1. Clone the repository**

```bash
git clone https://github.com/ahmed-sial/getthebook.git
cd getthebook
```

**2. Start PostgreSQL**

```bash
docker-compose up postgres -d
```

**3. Run the application**

```bash
./mvnw spring-boot:run
```

The app starts on port `8080` using the `dev` profile by default.

---

## Configuration

All config lives in `src/main/resources/`.

| Property | Default (dev) | Description |
|---|---|---|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/book_social_network` | PostgreSQL connection URL |
| `spring.datasource.username` | `appuser` | DB username |
| `spring.datasource.password` | `password` | DB password |
| `spring.jpa.hibernate.ddl-auto` | `update` | Schema strategy |
| `application.security.jwt.expiration` | `86400000` (24 h) | JWT expiry in milliseconds |
| `application.security.jwt.key` | *(set in dev yaml)* | HMAC signing key — **change in production** |
| `server.servlet.context-path` | `/api/v1` | Global API prefix |
| `spring.servlet.multipart.max-file-size` | `50MB` | Max upload size (for book covers) |

Environment variables available in the Docker image:

| Variable | Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://postgres-bsn:5432/book_social_network` | Datasource URL |
| `ACTIVE_PROFILE` | `dev` | Spring profile to activate |
| `APP_VERSION` | `1.0.0` | Matches the JAR version |

---

## API Documentation

Swagger UI is available when the application is running:

```
http://localhost:8080/api/v1/swagger-ui/index.html
```

OpenAPI JSON spec:

```
http://localhost:8080/api/v1/v3/api-docs
```

All secured endpoints require clicking **Authorize** in Swagger and providing a `Bearer <token>` obtained from the `/auth/login` endpoint.

---

## Running Tests

```bash
./mvnw test
```

Integration tests use **Testcontainers**, which automatically pulls and starts a PostgreSQL container — no manual setup needed. Make sure Docker is running before executing tests.

```bash
# Run only unit tests (skips integration tests)
./mvnw test -Dtest="**/unit/**"
```

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "feat: describe your change"`
4. Push to your fork: `git push origin feature/your-feature`
5. Open a Pull Request

Please follow standard Java / Spring Boot conventions and ensure all tests pass before submitting.

---

> Built with ☕ Java 21 and Spring Boot 4.
