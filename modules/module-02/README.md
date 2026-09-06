# Module 02 — Identity & Access

## Status: VERIFIED

## Overview

M2 implements the Identity & Access foundation for EcoSort 2.0. It provides a minimal but real authentication and authorization system suitable for the current Spring Boot REST architecture.

## Scope

M2 owns:
- User identity representation
- Password-based authentication with secure hashing
- Login / authentication flow
- Stateless JWT-based authenticated sessions
- Authorization checks for protected endpoints
- Authenticated-user identity propagation to downstream code
- User-scoped access control foundation

## Non-Responsibilities

M2 does NOT implement:
- Waste domain models or classification logic
- MongoDB persistence (M3 responsibility)
- External identity providers (OAuth2, LDAP, etc.)
- Role-based access control beyond a base authenticated role
- Password reset, email verification, or account recovery
- Frontend or UI components
- Swachhata integration or disposal recommendations

## Authentication Flow

1. **Registration** — `POST /api/auth/register`
   - Accepts `email`, `password` (min 8 chars), `name`
   - Validates input via Jakarta Validation
   - Hashes password with BCrypt (`BCryptPasswordEncoder`)
   - Creates a `User` record with a generated UUID
   - Returns `201 Created` with JWT token and user profile

2. **Login** — `POST /api/auth/login`
   - Accepts `email`, `password`
   - Looks up user by email in the in-memory store
   - Verifies password against stored BCrypt hash
   - Returns `200 OK` with JWT token and user profile

3. **Token Usage**
   - Client includes `Authorization: Bearer <token>` header
   - `JwtAuthFilter` intercepts requests, validates JWT signature and expiration
   - On success, sets `Authentication` in `SecurityContextHolder` with `UserPrincipal`
   - On failure, clears context and returns `401 Unauthorized`

4. **Protected Endpoint** — `GET /api/auth/me`
   - Requires valid JWT
   - Returns the authenticated user's profile via `CurrentUser.get()`

## Authorization Behavior

- All endpoints require authentication by default
- Exemptions: `/api/auth/register`, `/api/auth/login`, `/actuator/health`, `/actuator/health/**`, `/actuator/info`
- Spring Security `SecurityFilterChain` enforces rules server-side
- CSRF is disabled (stateless REST API)
- Sessions are stateless (`SessionCreationPolicy.STATELESS`)
- `CurrentUser.get()` extracts identity from `SecurityContextHolder` — downstream code never trusts client-supplied user IDs

## Identity Propagation Mechanism

- `JwtAuthFilter` validates the JWT and wraps the `User` in a `UserPrincipal` (implements `UserDetails`)
- `UserPrincipal` is stored in `SecurityContextHolder.getContext().getAuthentication()`
- `CurrentUser.get()` reads the `UserPrincipal` from the security context and returns the `User` record
- Controllers and services obtain the authenticated identity via `CurrentUser.get()`

## Persistence Boundary

M2 uses an in-memory user store (`InMemoryUserRepository`) behind the `UserRepository` interface. This satisfies M2's requirement for a functional and testable authentication flow without introducing MongoDB.

- `UserRepository` interface defines the persistence contract
- `InMemoryUserRepository` implements it using `ConcurrentHashMap`
- M3 can introduce MongoDB persistence by implementing `UserRepository` without changing authentication logic

M2 does NOT add MongoDB repositories, Mongo configuration, or waste-domain persistence.

## API Contracts

### Register

`POST /api/auth/register`

**Request:**
```json
{
  "email": "user@example.com",
  "password": "securepassword",
  "name": "User Name"
}
```

**Response 201:**
```json
{
  "token": "<jwt>",
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "name": "User Name",
    "createdAt": "2024-01-01T00:00:00Z"
  },
  "expiresAt": "2024-01-02T00:00:00Z"
}
```

**Validation errors:** `400 Bad Request` with `ErrorResponse`
**Duplicate email:** `400 Bad Request`

### Login

`POST /api/auth/login`

**Request:**
```json
{
  "email": "user@example.com",
  "password": "securepassword"
}
```

**Response 200:**
```json
{
  "token": "<jwt>",
  "user": { ... },
  "expiresAt": "2024-01-02T00:00:00Z"
}
```

**Invalid credentials:** `401 Unauthorized` with `ErrorResponse`

### Get Current User

`GET /api/auth/me`

**Headers:** `Authorization: Bearer <jwt>`

**Response 200:**
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "name": "User Name",
  "createdAt": "2024-01-01T00:00:00Z"
}
```

**Unauthenticated:** `401 Unauthorized`

## Error Responses

All errors use the existing M1 `ErrorResponse` format:

```json
{
  "timestamp": "2024-01-01T00:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/auth/me"
}
```

## Security Considerations

- Passwords are never stored as plaintext — BCrypt hashing is enforced
- JWT signing key is externalized via `APP_JWT_SECRET` environment variable
- A development placeholder default exists in `application.yml` but MUST be overridden in production
- Tokens have configurable expiration (default 24 hours)
- No secrets, credentials, or tokens are committed to the repository
- Authentication and authorization failures return generic messages without implementation details
- `CurrentUser.get()` is the only supported way to obtain the authenticated identity

## Package Structure

```
com.ecosort.auth.model        — User record
com.ecosort.auth.dto          — RegisterRequest, LoginRequest, AuthResponse, UserResponse
com.ecosort.auth.repository    — UserRepository interface, InMemoryUserRepository
com.ecosort.auth.service       — UserService, JwtService
com.ecosort.auth.config        — JwtProperties (configuration properties)
com.ecosort.auth.exception     — AuthException
com.ecosort.auth.controller    — AuthController, MeController
com.ecosort.security           — UserPrincipal, CurrentUser, JwtAuthFilter
com.ecosort.config             — SecurityConfig, GlobalExceptionHandler (updated)
```

## Tests

Focused M2 tests (15 total):

| Test Class | Coverage |
|------------|----------|
| `SecurityContextTest` | Application context starts with security configured |
| `AuthControllerTest` | Registration, login, protected endpoint, identity propagation, auth failures |
| `UserServiceTest` | Password hashing, duplicate email rejection |
| `JwtServiceTest` | Token generation, validation, empty secret rejection |

Key scenarios covered:
- Successful registration
- Password stored as BCrypt hash (not plaintext)
- Successful login returns JWT
- Invalid credentials rejected with 401
- Non-existent user login rejected with 401
- Protected endpoint rejects unauthenticated access (401)
- Authenticated request reaches protected endpoint (200)
- Authenticated identity correctly propagated (`/api/auth/me` returns correct user)
- Invalid JWT rejected (401)
- Duplicate registration rejected (400)

## Verification Procedure

```bash
# Focused M2 tests
cd backend
mvn test -Dtest="SecurityContextTest,AuthControllerTest,UserServiceTest,JwtServiceTest"

# Full regression (M1 + M2)
mvn test

# Clean package
mvn clean package -DskipTests
```

All 21 backend tests pass (6 M1 + 15 M2).

## Dependencies Added

- `spring-boot-starter-security` — Spring Security framework
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` 0.12.5 — JWT token generation and validation

## Completion

M2 implementation is complete and verified. All authentication, authorization, and identity propagation requirements are satisfied. The in-memory user store provides a clean persistence boundary for M3 to replace with MongoDB.
