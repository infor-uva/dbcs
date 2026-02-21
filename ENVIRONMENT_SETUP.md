# Environment Setup Guide

This guide explains how to set up your local environment for the OAuth2 Authorization Server and related microservices.

## ⚠️ Security Notice

**NEVER commit `.env` files or any credentials to version control.**

All sensitive information should be:

1. Stored in `.env` files (which are ignored by git)
2. Provided via environment variables
3. Managed through a secure secrets management system in production

## Getting Started

### 1. Create Local Environment File

Copy the example environment file and configure it with your actual values:

```bash
cp .env.example .env
# Edit .env with your actual credentials
nano .env  # or your preferred editor
```

### 2. OAuth2 Provider Setup

#### GitHub OAuth App

1. Go to https://github.com/settings/developers
2. Click "New OAuth App"
3. Fill in the application details:
    - Application name: `dbcs-auth`
    - Homepage URL: `http://localhost:8101`
    - Authorization callback URL: `http://localhost:8101/login/oauth2/code/github`
4. Copy the Client ID and Client Secret to `.env`:
   ```
   OAUTH2_GITHUB_CLIENT_ID=your_client_id
   OAUTH2_GITHUB_SECRET=your_client_secret
   ```

#### Google OAuth

1. Go to https://console.cloud.google.com/
2. Create a new project or select existing
3. Enable Google+ API
4. Go to Credentials → Create OAuth 2.0 Client IDs
5. Select "Web application"
6. Add authorized redirect URIs:
    - `http://localhost:8101/login/oauth2/code/google`
7. Copy the Client ID and Client Secret to `.env`:
   ```
   GOOGLE_CLIENT_ID=your_client_id.apps.googleusercontent.com
   GOOGLE_CLIENT_SECRET=your_client_secret
   ```

### 3. Database Setup

Ensure MySQL is running locally or via Docker:

```bash
# Docker approach (recommended)
docker run --name mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=Auth \
  -e MYSQL_USER=user \
  -e MYSQL_PASSWORD=password \
  -p 3306:3306 \
  -d mysql:8.0
```

Update `.env` with database credentials:

```
DB_HOST=localhost
DB_PORT=3306
DB_NAME=Auth
DB_USERNAME=user
DB_PASSWORD=password
```

### 4. Redis Setup

Ensure Redis is running:

```bash
# Docker approach (recommended)
docker run --name redis \
  -p 6379:6379 \
  -d redis:7-alpine
```

Update `.env`:

```
REDIS_HOST=localhost
REDIS_PORT=6379
```

### 5. Run Application

#### Using Maven

```bash
cd java/services/auth
mvn spring-boot:run
```

The auth service will start at `http://localhost:8101`

#### Using Docker Compose

```bash
docker-compose up
```

### 6. Development Server (Angular)

If developing the frontend:

```bash
cd angular/RestClient
npm install
ng serve --open
```

Frontend will be available at `http://localhost:4200`

## Environment Variables Reference

### OAuth2 Credentials (Required for OAuth2 features)

- `OAUTH2_GITHUB_CLIENT_ID` - GitHub OAuth app client ID
- `OAUTH2_GITHUB_SECRET` - GitHub OAuth app client secret
- `GOOGLE_CLIENT_ID` - Google OAuth client ID
- `GOOGLE_CLIENT_SECRET` - Google OAuth client secret

### Database (Required)

- `DB_HOST` - MySQL host (default: localhost)
- `DB_PORT` - MySQL port (default: 3306)
- `DB_NAME` - Database name (default: Auth)
- `DB_USERNAME` - Database user (default: user)
- `DB_PASSWORD` - Database password (default: password)

### Redis (Required for token revocation & rate limiting)

- `REDIS_HOST` - Redis host (default: localhost)
- `REDIS_PORT` - Redis port (default: 6379)
- `REDIS_PASSWORD` - Redis password (optional)

### JWT Configuration

- `JWT_SECRET_KEY` - Secret key for JWT (minimum 32 characters)
- `JWT_KID` - Key ID identifier
- `JWT_ACCESS_TOKEN_EXPIRY` - Access token TTL in seconds (default: 900)
- `JWT_REFRESH_TOKEN_EXPIRY` - Refresh token TTL in seconds (default: 604800)

### CORS

- `CORS_ORIGINS` - Comma-separated list of allowed origins

### Logging

- `LOG_LEVEL` - Log level (INFO, DEBUG, WARN, ERROR)
- `SECURITY_LOG_LEVEL` - Security-specific log level

## Troubleshooting

### Port Already in Use

If you get "Address already in use" error:

```bash
# Find process using the port
lsof -i :8101

# Kill the process
kill -9 <PID>
```

### Database Connection Issues

```bash
# Test MySQL connection
mysql -h localhost -u user -p -e "SELECT 1"
```

### Redis Connection Issues

```bash
# Test Redis connection
redis-cli ping
# Should return: PONG
```

### OAuth2 Redirect URI Mismatch

Make sure the redirect URIs in your OAuth2 provider settings exactly match:

- GitHub: `http://localhost:8101/login/oauth2/code/github`
- Google: `http://localhost:8101/login/oauth2/code/google`

Note the exact domain, port, and path.

## Production Considerations

For production deployment:

1. **Use Secret Management System**
    - AWS Secrets Manager
    - HashiCorp Vault
    - Azure Key Vault
    - Kubernetes Secrets

2. **Environment Variables Priority**
   ```
   Secrets Management System > Environment Variables > .env (local only)
   ```

3. **Never Log Secrets**
    - Filter out sensitive data from logs
    - Use `SecurityConfig` to prevent exposure

4. **HTTPS Only**
    - Enable HTTPS enforcement in production
    - Update OAuth2 redirect URIs to use `https://`

5. **Rotate Keys Regularly**
    - JWT key rotation is automated (every 30 days)
    - OAuth2 credentials should be rotated quarterly
    - Database passwords should be rotated regularly

## IDE Configuration

### IntelliJ IDEA

1. Run → Edit Configurations
2. Add environment variables from `.env` to your run configuration

### VS Code

1. Create `.vscode/launch.json`:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "name": "Java: Auth Service",
      "type": "java",
      "name": "Spring Boot App",
      "request": "launch",
      "envFile": "${workspaceFolder}/.env"
    }
  ]
}
```

## Additional Resources

- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [OAuth2 Specification](https://oauth.net/2/)
- [GitHub OAuth Documentation](https://docs.github.com/en/developers/apps/building-oauth-apps)
- [Google OAuth Documentation](https://developers.google.com/identity/protocols/oauth2)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

---

**Last Updated**: February 21, 2026

For questions or issues, refer to the project documentation or contact the development team.

