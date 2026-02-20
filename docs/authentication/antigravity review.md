This plan aproach is ok?
Refactor JWT Logic to Shared Library
This plan aims to eliminate duplicated JWT validation logic across microservices and create a "Single Source of Truth" (
SST) in a shared Maven library.

User Review Required
IMPORTANT

This change introduces a shared library jwt-utils. All microservices will need to be rebuilt and the library must be
installed in the local Maven repository (mvn install) for the services to build successfully.

NOTE

We will move from a "Service-Call-to-Auth" validation model to a "Local-Validation" model in the shared library. This
requires the secret-key to be present in all services (can be provided via environment variables or Spring Cloud
Config).

Proposed Changes
[NEW] Shared Library: jwt-utils
Summary: Create a new Maven project in java/common/jwt-utils to house all common JWT logic.

[NEW]
pom.xml
Define project metadata (groupId: com.uva.common, artifactId: jwt-utils).
Add dependencies: java-jwt, vavr, lombok, spring-boot-starter-security, spring-boot-starter-web.
[NEW]
UserRol.java
Unified enum for user roles (ADMIN, MANAGER, CLIENT).
[NEW]
Service.java
Unified enum for internal services.
[NEW]
JwtData.java
Common DTO for decoded JWT data.
[NEW]
JwtUtil.java
Extracted logic for validating and decoding tokens locally using the shared secret.
[NEW]
JwtAuthenticationFilter.java
Extracted Spring Security filter for JWT authentication.
[MODIFY] Microservices Refactoring
Summary: Update auth, hotels, users, and bookings to use the new shared library.

[MODIFY]
pom.xml
[MODIFY]
pom.xml
[MODIFY]
pom.xml
[MODIFY]
pom.xml
Add com.uva.common:jwt-utils dependency.
Remove redundant dependencies (if any).
[DELETE] Duplicated Files
Remove
JwtAuthenticationFilter.java
,
JwtData.java
,
UserRol.java
,
Service.java
,
TokenService.java
, and
TokenAPI.java
from each service.
[MODIFY] Security Configuration
Update
SecurityConfig.java
in each service to use the shared
JwtAuthenticationFilter
.
Verification Plan
Automated Verification
I will attempt to build the shared library using mvn clean install.
I will then attempt to build each microservice using mvn clean compile to ensure no breaking changes in dependencies or
class names.
Manual Verification
Since I don't have a full running environment with a database, I will rely on successful compilation as the primary
verification.
I will verify that the
JwtUtil
in the shared library correctly handles the HMAC validation logic by cross-referencing with the original implementation
in the auth service.

Comment
⌥⌘M