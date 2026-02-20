# Plan: OAuth2 + Password Auth → JWT Stateless Sessions

Implement a hybrid authentication system supporting OAuth2 (GitHub/Google) and traditional username/password login,
unifying both with JWT-based stateless sessions (access + refresh tokens). The service creates or updates local users
from OAuth2 data and issues JWT tokens for all authentication flows.

## Steps

### 1. Enhance `UserEntity`

**File:** `java/services/auth/src/main/java/com/uva/api/auth/modules/user/UserEntity.java`

- Add fields for OAuth2 metadata (`providerUserId`, optional `password`)
- Add audit timestamps (`createdAt`, `updatedAt`)
- Add account status flags (`enabled`, `verified`)
- Ensure nullable password field for OAuth2-only users

### 2. Extend `AuthService`

**File:** `java/services/auth/src/main/java/com/uva/api/auth/modules/auth/AuthService.java`

- Add method for password-based login with validation
- Add method for registration with email verification preparation
- Add OAuth2 user sync logic (find-or-create)
- Validate credentials and throw appropriate exceptions

### 3. Complete `OAuth2LoginSuccessHandler`

**File:** `java/services/auth/src/main/java/com/uva/api/auth/config/OAuth2LoginSuccessHandler.java`

- Finish OAuth2 user creation/update logic
- Extract provider metadata (email, name, provider ID)
- Handle account linking strategy (auto-link or require confirmation)
- Issue JWT tokens via `TokenService` after successful OAuth2 authentication

### 4. Expand `AuthController` Endpoints

**File:** `java/services/auth/src/main/java/com/uva/api/auth/modules/auth/AuthController.java`

- Add `POST /auth/login` — password-based login
- Add `POST /auth/register` — user registration with email
- Add `POST /auth/refresh` — refresh token rotation
- Add `POST /auth/logout` — revoke refresh token
- Add `GET /auth/user` — current user info extracted from JWT

### 5. Configure `SecurityConfig`

**File:** `java/services/auth/src/main/java/com/uva/api/auth/config/SecurityConfig.java`

- Update filter chain for both OAuth2 login flows and stateless JWT validation
- Ensure CSRF disabled for stateless REST API
- Configure session policy (STATELESS)
- Add CORS configuration if needed for frontend
- Whitelist public endpoints (`/auth/login`, `/auth/register`, `/auth/refresh`, `/oauth2/**`, `/login/oauth2/**`)

### 6. Add JWT Filter + Enhanced Utilities

**File:** Create `java/services/auth/src/main/java/com/uva/api/auth/modules/jwt/JwtAuthenticationFilter.java` (new)

- Implement `OncePerRequestFilter` to validate access tokens on protected endpoints
- Extract user claims from JWT
- Set authentication context for downstream processing

**File:** Enhance `java/services/auth/src/main/java/com/uva/api/auth/modules/jwt/JwtUtil.java`

- Add token claims extraction and validation logic
- Add token expiration checking
- Add claims parsing for user ID, email, and roles

## Further Considerations

### 1. Email Verification Workflow

- For local registration, should we require email verification before issuing tokens, or allow login immediately?
- Consider adding optional email verification endpoint (`POST /auth/verify-email`)
- Store verification tokens in database with expiration

### 2. Account Linking Strategy

- When an OAuth2 user logs in with an email that matches an existing local account, should we:
    - Auto-link them (merge accounts)?
    - Require manual linking confirmation?
    - Block and require user intervention?

### 3. Roles/Permissions Model

- Current `UserRol` is simple enum (`CLIENT`, etc.)
- Do you need granular role-based access control (RBAC)?
- Do you need scope-based authorization for OAuth2?

### 4. Token Management

- Consider adding token revocation/blacklist for logout (currently using refresh token table)
- Consider adding token introspection endpoint for other services
- Implement token rotation strategy (already in place with refresh tokens)

### 5. Security Hardening

- Add rate limiting on login/register endpoints
- Add CAPTCHA for registration (optional)
- Add login attempt tracking and account lockout
- Validate OAuth2 redirect URIs properly
- Use environment variables for sensitive keys (already configured)

### 6. Error Handling & Validation

- Create custom exceptions for auth flows (InvalidCredentialsException, etc.)
- Add detailed validation messages for registration
- Implement global exception handler for auth endpoints
- Return consistent error response format

### 7. User Synchronization

- For OAuth2 users, decide on profile update strategy:
    - Always sync latest profile data from provider?
    - Sync only on first login?
    - Allow manual user profile edits?

### 8. API Response Format

- Define standard response DTOs for:
    - Login response (access token, refresh token, user info)
    - Registration response
    - Error responses
    - User profile response

