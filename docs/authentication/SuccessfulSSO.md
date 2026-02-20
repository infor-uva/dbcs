> ## After a SSO authentication in spring boot, how i can extract the user data to registry as my application user?

In a **Spring Boot** application using **SSO (Single Sign-On)**, after the user successfully authenticates with the
Identity Provider (IdP), you typically get a `Principal` or `Authentication` object that contains the user’s data. You
can then extract this information to create or update a local application user in your registry or database.

Here’s a step-by-step guide for a common setup (Spring Security + OAuth2 / OpenID Connect):

---

### 1. Configure OAuth2 SSO in Spring Boot

In `application.yml` or `application.properties`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          keycloak: # or your IdP (Okta, Auth0, etc.)
            client-id: your-client-id
            client-secret: your-client-secret
            scope: openid, profile, email
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
        provider:
          keycloak:
            issuer-uri: https://your-keycloak-domain/auth/realms/your-realm
```

---

### 2. Create a Custom OAuth2 User Service

Spring Security provides a way to handle the user info from the IdP using `OAuth2UserService`.

```java
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) {
    OAuth2User oauthUser = super.loadUser(userRequest);

    // Extract user info
    String email = oauthUser.getAttribute("email");
    String name = oauthUser.getAttribute("name");
    String id = oauthUser.getName(); // Usually the sub or unique Id

    // Now you can register or update your local user
    registerOrUpdateUser(id, name, email);

    return oauthUser;
  }

  private void registerOrUpdateUser(String id, String name, String email) {
    // Example: lookup user in DB, if not found create one
    // userRepository.findById(id).orElseGet(() -> userRepository.save(new User(...)))
  }
}
```

---

### 3. Plug the Custom User Service into Security Configuration

```java
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

@EnableWebSecurity
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;

  public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
    this.customOAuth2UserService = customOAuth2UserService;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests(auth -> auth
        .anyRequest().authenticated()
      )
      .oauth2Login(oauth2 -> oauth2
        .userInfoEndpoint(userInfo -> userInfo
          .userService(customOAuth2UserService)
        )
      );
    return http.build();
  }
}
```

---

### 4. Alternative: Access User in Controllers

Sometimes you might just want to access the authenticated user in a controller:

```java
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  @GetMapping("/me")
  public String me(@AuthenticationPrincipal OAuth2User principal) {
    String email = principal.getAttribute("email");
    String name = principal.getAttribute("name");
    return "Hello " + name + " (" + email + ")";
  }
}
```

---

✅ **Key points**:

1. `OAuth2User` (for OAuth2/OIDC) or `Principal` (for generic SSO) contains the authenticated user data.
2. Use a **custom `OAuth2UserService`** to intercept authentication and create/update your local app user.
3. You can store additional information in your DB or `UserDetails` for Spring Security later.
4. Make sure to map IdP attributes (`email`, `name`, `sub`, etc.) correctly.

---

If you want, I can provide a **full working example** where after SSO login, the app **automatically creates a local
user in the database** with role assignment and token storage. This is often the pattern used in production.

Do you want me to do that?
