To apply role-based filtering across your components or methods, you are looking for **Spring Security**. Depending on whether you chose the **RestController** or **Functional (Router/Handler)** approach, the implementation differs slightly, but the core concept remains the same: **Method-Level Security**.

Here is how you configure it.

### 1. The Configuration (The "Doorman")

First, you must enable Method Security in a configuration class. This allows you to use annotations directly on your methods.

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // This is the key for @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}

```

---

### 2. Filtering for RestControllers

For the annotation-based approach, you use `@PreAuthorize`. This is highly granular; you can put it on the entire class or on a single method.

```java
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Entire class requires ADMIN role
public class AdminController {

    @GetMapping("/dashboard")
    public String getDashboard() {
        return "Welcome, Admin!";
    }

    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasAuthority('SCOPE_delete')") // Specific method requirement
    public String deleteUser(@PathVariable String id) {
        return "User deleted";
    }
}

```

---

### 3. Filtering for Router + Handler

In the functional approach, you usually handle the security at the **Router** level using a filter. This keeps your Handler logic "clean" of security checks.

```java
@Configuration
public class SecuredRouter {

    @Bean
    public RouterFunction<ServerResponse> securedRoutes(UserHandler handler) {
        return RouterFunctions.route()
            .GET("/api/secret", handler::handleSecret)
            .filter((request, next) -> {
                // Manual check or let Spring Security handle it via the FilterChain
                return next.handle(request);
            })
            .build();
    }
}

```

---

### How Authorization Works

1. **Authentication:** The system identifies who you are (e.g., "User: Bob").
2. **Authorities/Roles:** Bob is assigned `ROLE_USER`.
3. **Access Decision:** When Bob hits a method marked `@PreAuthorize("hasRole('ADMIN')")`, the `MethodSecurityInterceptor` intercepts the call, sees the mismatch, and throws an `AccessDeniedException`.

### Summary of Annotations

* `@PreAuthorize("hasRole('ADMIN')")`: Checks role before the method runs (most common).
* `@PostAuthorize`: Runs the method, but checks if the user is allowed to see the *result* before returning it.
* `@Secured("ROLE_ADMIN")`: An older, less powerful version of PreAuthorize.

Would you like me to show you how to set up the **UserDetailsService** so you can test these roles with hardcoded users in memory?